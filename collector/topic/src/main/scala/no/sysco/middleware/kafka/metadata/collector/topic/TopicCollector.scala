package no.sysco.middleware.kafka.metadata.collector.topic

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import no.sysco.middleware.kafka.metadata.collector.topic.internal._

import scala.concurrent.ExecutionContext

object TopicCollector {
  implicit val system: ActorSystem = ActorSystem("kafka-metadata-collector-topic")
  new TopicCollector(system)
}

/**
  * Collect Topic events by observing changes a Kafka Cluster.
  */
class TopicCollector(system: ActorSystem) {

  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)

  val config: Config = ConfigFactory.load()
  val appConfig: TopicCollectorConfig = new TopicCollectorConfig(config)

  val topicEventProducer: ActorRef =
    system.actorOf(TopicEventProducer.props(appConfig.Collector.topicEventTopic, appConfig.Kafka.bootstrapServers))
  val topicRepository: ActorRef =
    system.actorOf(TopicRepository.props(appConfig.Kafka.bootstrapServers))
  val topicManager: ActorRef =
    system.actorOf(TopicManager.props(appConfig.Collector.pollFrequency, topicRepository, topicEventProducer))
  val topicEventConsumer: TopicEventConsumer =
    TopicEventConsumer(topicManager, appConfig.Collector.topicEventTopic, appConfig.Kafka.bootstrapServers)

  topicManager ! CollectTopics
}
