package no.sysco.middleware.metadata.api.application

import java.util.Properties
import java.util.stream.Collectors

import akka.actor.{Actor, ActorLogging, Props}
import no.sysco.middleware.metadata.api.ApplicationConfig
import no.sysco.middleware.metadata.api.application.KafkaService.RegisterTopicMetadata
import no.sysco.middleware.metadata.api.application.ObserverActor.Initialize
import no.sysco.middleware.metadata.api.infrastructure.Topics
import no.sysco.middleware.metadata.api.rest.{TopicMetadata, TopicMetadataJsonProtocol}
import org.apache.kafka.clients.admin._
import org.apache.kafka.common.errors.TopicExistsException

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, ExecutionException}

object ObserverActor {
  def props(config: ApplicationConfig, kafkaService: KafkaService)(implicit executionContext: ExecutionContext): Props = {
    Props(new ObserverActor(config, kafkaService))
  }

  sealed trait Command
  case object Initialize extends Command
  case object FirstLookup extends Command
}

class ObserverActor(config: ApplicationConfig, kafkaService: KafkaService) extends Actor with ActorLogging with TopicMetadataJsonProtocol {

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
