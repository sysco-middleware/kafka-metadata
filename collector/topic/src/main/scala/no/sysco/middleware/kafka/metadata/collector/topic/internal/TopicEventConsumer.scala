package no.sysco.middleware.kafka.metadata.collector.topic.internal

import akka.actor.{Actor, ActorRef, Props}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.typesafe.config.{Config, ConfigFactory}
import no.sysco.middleware.kafka.metadata.collector.proto.topic.TopicEventPb
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

object TopicEventConsumer {
  def props(topicManager: ActorRef)(implicit materializer: ActorMaterializer): Props =
    Props(new TopicEventConsumer(topicManager))
}

/**
  * Consume Topic events.
  * @param topicManager Reference to Topic Manager, to consume events further.
  */
class TopicEventConsumer(topicManager: ActorRef)(implicit materializer: ActorMaterializer)
  extends Actor {

  val config: Config = ConfigFactory.load() //TODO fix

  val consumerSettings: ConsumerSettings[String, Array[Byte]] =
    ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("group1")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val consumerSource: Consumer.Control =
    Consumer.plainSource(consumerSettings, Subscriptions.topics("topic"))
      .map(record => TopicEventPb.parseFrom(record.value()))
      .map(topicEvent => topicManager ! topicEvent)
      .to(Sink.ignore)
      .run()(materializer)

  override def receive: Receive = _

}
