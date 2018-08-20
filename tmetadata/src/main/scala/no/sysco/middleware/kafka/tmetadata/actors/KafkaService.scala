package no.sysco.middleware.kafka.tmetadata.actors


import java.util.Properties

import akka.actor.{Actor, Props}
import akka.http.scaladsl.server.Route
import no.sysco.middleware.kafka.tmetadata.rest.TopicMetadata
import no.sysco.middleware.kafka.tmetadata.{ApplicationConfig, Env}
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

final case class RegisterTopicMetadataCommand(json: TopicMetadata)

final case class ResultEvent(success: Boolean = true, message: String = "")


trait KafkaServiceActor extends Actor

object KafkaService {
  def props(config: ApplicationConfig)(implicit executionContext: ExecutionContext) : Props= {
    config.env match {
      case Env.dev  =>  Props(new KafkaRepository(config))
      case Env.test =>  Props(new MockService(config))
    }
  }
}

class KafkaRepository(config: ApplicationConfig)(implicit executionContext: ExecutionContext) extends KafkaServiceActor {
  private val topic = "topics-metadata"

  private val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka.bootstrapServers)
  props.put(ProducerConfig.ACKS_CONFIG, "all")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
  val kafkaProducer: KafkaProducer[String, String] = new KafkaProducer(props)

  override def receive: Receive = {
    case command: RegisterTopicMetadataCommand => {
      println(command)
      sender() ! registerSync(command)
    }
    case _ => {}
  }

  private def registerSync(command: RegisterTopicMetadataCommand):Future[ResultEvent] = {
    val record = new ProducerRecord[String, String](topic, command.json.topicName, command.json.toString)
    try {
      kafkaProducer.send(record)
      Future(ResultEvent())
    } catch {
      case e:Exception =>
        e.printStackTrace()
        Future(ResultEvent(success = false, message = e.getMessage))
    }
  }

  private def registerAsync(command: RegisterTopicMetadataCommand):Future[ResultEvent] = {
    val record = new ProducerRecord[String, String](topic, command.json.topicName, command.json.toString)
    val promise = Promise[ResultEvent]()
    kafkaProducer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        val result = if (exception == null) ResultEvent() else ResultEvent(success = false, exception.getMessage)
        promise.complete(Try(result))
      }
    })
    promise.future
  }

}

class MockService(config: ApplicationConfig) extends KafkaServiceActor {


  override def receive: Receive = {
    case command:RegisterTopicMetadataCommand => {
      println(s"I am mocked repo, here is command: $command")
    }
    case _ => {}
  }
}
