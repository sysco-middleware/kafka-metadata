package no.sysco.middleware.kafka.metadata.collector.topic.internal

import akka.actor.ActorRef
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.typesafe.config.{Config, ConfigFactory}
import no.sysco.middleware.kafka.metadata.collector.proto.topic.TopicEventPb
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

object TopicEventConsumer {
  def apply(topicManager: ActorRef, topicEventTopic: String, bootstrapServers: String)(implicit materializer: ActorMaterializer): TopicEventConsumer =
    new TopicEventConsumer(topicManager, topicEventTopic, bootstrapServers)
}

/**
  * Consume Topic events.
  * @param topicManager Reference to Topic Manager, to consume events further.
  */
class TopicEventConsumer(topicManager: ActorRef, topicEventTopic: String, bootstrapServers: String)(implicit materializer: ActorMaterializer) {

  val config: Config = ConfigFactory.load()

  val consumerSettings: ConsumerSettings[String, Array[Byte]] =
    ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId("kafka-metadata-collector-topic-internal-consumer")
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val consumerSource: Consumer.Control =
    Consumer.plainSource(consumerSettings, Subscriptions.topics(topicEventTopic))
      .map(record => TopicEventPb.parseFrom(record.value()))
      .map(topicEvent => topicManager ! topicEvent)
      .to(Sink.ignore)
      .run()(materializer)

}
