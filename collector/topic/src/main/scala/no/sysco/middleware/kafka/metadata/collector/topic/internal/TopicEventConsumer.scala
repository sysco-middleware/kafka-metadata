package no.sysco.middleware.kafka.metadata.collector.topic.internal

import akka.actor.ActorRef
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.typesafe.config.ConfigFactory
import no.sysco.middleware.kafka.metadata.collector.proto.topic.TopicEventPb
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

class TopicEventConsumer(topicManager: ActorRef)(implicit materializer: ActorMaterializer) {

  val config = ConfigFactory.load() //TODO fix

  val consumerSettings =
    ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("group1")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val consumerSource =
    Consumer.plainSource(consumerSettings, Subscriptions.topics("topic"))
      .map(record => TopicEventPb.parseFrom(record.value()))
      .map(topicEvent => topicManager ! topicEvent)
      .to(Sink.ignore)
      .run()
}
