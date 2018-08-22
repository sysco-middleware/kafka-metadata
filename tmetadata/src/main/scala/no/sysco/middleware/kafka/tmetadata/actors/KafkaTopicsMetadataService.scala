package no.sysco.middleware.kafka.tmetadata.actors


import java.util.concurrent.CountDownLatch

import akka.actor.{Actor, ActorLogging, Props}
import no.sysco.middleware.kafka.tmetadata.infrastructure.{KafkaTopicsMetadataRepositoryRead, KafkaTopicsMetadataRepositoryWrite}
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
  sealed trait Event

  final case class RegisterTopicMetadata(json: TopicMetadata) extends Command
  final case class RegisteredTopicMetadataAttempt(success: Boolean = true, message: String = "") extends Event

  final case class FetchTopicsMetadata() extends Command
  final case class FetchedTopicsMetadata(topicsMetadata : Seq[TopicMetadata]) extends Event
}

class KafkaServiceActor(config: ApplicationConfig) extends Actor with ActorLogging {
  val system = context.system
  import system.dispatcher //as implicit ExecutionContext

  import no.sysco.middleware.kafka.tmetadata.actors.KafkaService._

  val kafkaRepositoryWrite = KafkaTopicsMetadataRepositoryWrite.initRepository(config)
//  val kafkaRepositoryRead = KafkaTopicsMetadataRepositoryRead.initRepository(config)

  override def preStart(): Unit = {
//    kafkaRepositoryRead.addShutdownHook(new CountDownLatch(1), "kafka-streams-app")
    super.preStart()
  }

  override def receive: Receive = {
    case command: RegisterTopicMetadata => {
      log.info("Command got {}", command)
      sender() ! kafkaRepositoryWrite.registerSync(command)
    }
//    case command: FetchTopicsMetadata => {
//      log.info("Command got {}", command)
//      sender() ! kafkaRepositoryRead.topicsMetadata()
//    }
    case unexpected =>  log.error("Unexpected {}", unexpected)
  }




}

class MockService(config: ApplicationConfig) extends Actor with ActorLogging {
  import no.sysco.middleware.kafka.tmetadata.actors.KafkaService._

  override def receive: Receive = {
    case command: RegisterTopicMetadata => {
      println(s"I am mocked repo, here is command: $command")
    }
    case unexpected => log.error("Unexpected {}", unexpected)
  }
}
