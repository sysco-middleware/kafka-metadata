package no.sysco.middleware.kafka.tmetadata.actors


import akka.actor.{Actor, ActorLogging, Props}
import no.sysco.middleware.kafka.tmetadata.infrastructure.KafkaTopicsMetadataRepositoryWrite
import no.sysco.middleware.kafka.tmetadata.rest.TopicMetadata
import no.sysco.middleware.kafka.tmetadata.{ApplicationConfig, Env}

import scala.concurrent.ExecutionContext

object KafkaService {
  def props(config: ApplicationConfig)(implicit executionContext: ExecutionContext): Props = {
    config.env match {
      case Env.dev => Props(new KafkaServiceActor(config))
      case Env.test => Props(new MockService(config))
    }
  }

  sealed trait Command
  final case class RegisterTopicMetadataCommand(json: TopicMetadata) extends Command
  final case class ResultEvent(success: Boolean = true, message: String = "")
}

class KafkaServiceActor(config: ApplicationConfig) extends Actor with ActorLogging {
  val system = context.system
  import system.dispatcher //as implicit ExecutionContext

  import no.sysco.middleware.kafka.tmetadata.actors.KafkaService._


  val kafkaRepository = KafkaTopicsMetadataRepositoryWrite.initRepository(config)


  override def receive: Receive = {
    case command: RegisterTopicMetadataCommand => {
      log.info("Command got {}", command)
      sender() ! kafkaRepository.registerSync(command)
    }
    case unexpected =>  log.error("Unexpected {}", unexpected)
  }


}

class MockService(config: ApplicationConfig) extends Actor with ActorLogging {
  import no.sysco.middleware.kafka.tmetadata.actors.KafkaService._

  override def receive: Receive = {
    case command: RegisterTopicMetadataCommand => {
      println(s"I am mocked repo, here is command: $command")
    }
    case unexpected => log.error("Unexpected {}", unexpected)
  }
}
