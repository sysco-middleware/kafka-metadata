package no.sysco.middleware.kafka.metadata.collector.topic.internal

import akka.NotUsed
import akka.actor.Actor
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.{Config, ConfigFactory}
import no.sysco.middleware.kafka.metadata.collector.proto.topic.TopicEventPb
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

class TopicEventProducer(implicit materializer: ActorMaterializer) extends Actor {

  val config: Config = ConfigFactory.load() //TODO

  val producerSettings: ProducerSettings[String, Array[Byte]] =
    ProducerSettings(config, new StringSerializer(), new ByteArraySerializer())

  val producerSink: Sink[TopicEventPb, NotUsed] =
    Flow[TopicEventPb]
      .map(topicEvent =>
        new ProducerRecord[String, Array[Byte]](
          "topic",
          topicEvent.name,
          topicEvent.toByteArray))
        .to(Producer.plainSink(producerSettings))

  def handleTopicEvent(topicEvent: TopicEventPb): Unit = {
    Source.single(topicEvent)
      .to(producerSink)
      .run()
  }

  override def receive: Receive = {
    case topicEvent: TopicEventPb => handleTopicEvent(topicEvent)
  }
}
