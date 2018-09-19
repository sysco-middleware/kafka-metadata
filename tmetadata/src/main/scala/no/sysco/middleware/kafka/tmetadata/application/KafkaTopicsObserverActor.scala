package no.sysco.middleware.kafka.tmetadata.application

import java.util.Properties
import java.util.stream.Collectors

import akka.actor.{ AbstractLoggingActor, Actor, ActorLogging, Props }
import no.sysco.middleware.kafka.tmetadata.ApplicationConfig
import no.sysco.middleware.kafka.tmetadata.application.KafkaService.RegisterTopicMetadata
import no.sysco.middleware.kafka.tmetadata.application.KafkaTopicsObserverActor.Initialize
import no.sysco.middleware.kafka.tmetadata.infrastructure.Topics
import no.sysco.middleware.kafka.tmetadata.rest.{ TopicMetadata, TopicMetadataJsonProtocol }
import org.apache.kafka.clients.admin._
import org.apache.kafka.common.errors.TopicExistsException
import org.apache.kafka.common.requests.MetadataResponse

import scala.collection.JavaConverters._
import scala.concurrent.{ ExecutionContext, ExecutionException }

object KafkaTopicsObserverActor {
  def props(config: ApplicationConfig, kafkaService: KafkaService)(implicit executionContext: ExecutionContext): Props = {
    Props(new KafkaTopicsObserverActor(config, kafkaService))
  }

  sealed trait Command
  case object Initialize extends Command
  case object FirstLookup extends Command
}

class KafkaTopicsObserverActor(config: ApplicationConfig, kafkaService: KafkaService) extends Actor with ActorLogging with TopicMetadataJsonProtocol {
  import spray.json._

  val adminClient = admin

  override def receive: Receive = {
    case Initialize => {
      println(s"Kafka Topic Observer Actor Inited")

      // 1 create metadata-topics kafka topic
      createTopic(Seq(
        // todo: #Partitions & replicationFactor load from config
        new NewTopic(Topics.METADATA, 1, 1)))

      // 2 list existing (not internal)
      val commands = adminClient.listTopics(
        new ListTopicsOptions()
          .timeoutMs(500)
          .listInternal(false))
        .listings()
        .get()
        .stream()
        .map[String](_.name)
        .filter(!_.equalsIgnoreCase(Topics.METADATA))
        .map[RegisterTopicMetadata](topicName => RegisterTopicMetadata(TopicMetadata(topicName)))
        .collect(Collectors.toList[RegisterTopicMetadata])
        .asScala

      // 3 todo: handle restart, exclude registered topics

      // 4 register topics
      kafkaService.registerTopics(commands)

    }

    case unexpected => log.info("Unexpected {}", unexpected)
  }

  override def preStart = self ! Initialize

  def admin: AdminClient = {
    val props = new Properties()
    props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka.bootstrapServers)
    AdminClient.create(props)
  }

  def createTopic(newTopics: Seq[NewTopic]) = {
    try {
      val result = adminClient.createTopics(newTopics.asJava)
      result.all().get()
    } catch {
      case e: ExecutionException =>
        e.getCause match {
          case _: TopicExistsException =>
            println(s"Topics ${newTopics.map(_.name())} already exist")
        }
      case e: Throwable => throw e
    }
  }

}
