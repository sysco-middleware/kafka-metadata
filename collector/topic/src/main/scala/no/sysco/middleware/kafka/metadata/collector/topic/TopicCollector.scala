package no.sysco.middleware.kafka.metadata.collector.topic

import akka.actor.{ ActorRef, ActorSystem }
import akka.stream.ActorMaterializer
import com.typesafe.config.{ Config, ConfigFactory }
import no.sysco.middleware.kafka.metadata.collector.topic.internal._

import scala.concurrent.ExecutionContext

object TopicCollector extends App {
  implicit val system: ActorSystem = ActorSystem("kafka-metadata-collector-topic")
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  new TopicCollector()
}

/**
 * Collect Topic events by observing changes a Kafka Cluster.
 */
class TopicCollector(implicit actorSystem: ActorSystem, actorMaterializer: ActorMaterializer) {

  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val config: Config = ConfigFactory.load()
  val appConfig: TopicCollectorConfig = new TopicCollectorConfig(config)

  val topicManager: ActorRef =
    actorSystem.actorOf(
      TopicManager.props(
        appConfig.Collector.pollInteval,
        appConfig.Kafka.bootstrapServers,
        appConfig.Collector.topicEventTopic))

}
