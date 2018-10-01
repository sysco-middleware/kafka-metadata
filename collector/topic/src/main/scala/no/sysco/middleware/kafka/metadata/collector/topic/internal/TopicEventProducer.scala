package no.sysco.middleware.kafka.metadata.collector.topic.internal

import java.util.Properties

import akka.actor.{Actor, Props}
import no.sysco.middleware.kafka.metadata.collector.proto.topic.TopicEventPb
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

object TopicEventProducer {

  def props(topicEventTopic: String, bootstrapServers: String): Props = {
    val producerConfigs = new Properties()
    producerConfigs.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    producerConfigs.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, new StringSerializer().getClass.getName)
    producerConfigs.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new ByteArraySerializer().getClass.getName)
    val producer: Producer[String, Array[Byte]] = new KafkaProducer(producerConfigs)
    Props(new TopicEventProducer(topicEventTopic, producer))
  }

  def props(topicEventTopic: String, producer: Producer[String, Array[Byte]]) =
    Props(new TopicEventProducer(topicEventTopic, producer))
}

/**
  * Publish Topic events.
  */
class TopicEventProducer(topicEventTopic: String, producer: Producer[String, Array[Byte]]) extends Actor {

  def handleTopicEvent(topicEvent: TopicEventPb): Unit = {
    producer.send(new ProducerRecord(topicEventTopic, topicEvent.name, topicEvent.toByteArray))
  }

  override def receive: Receive = {
    case topicEvent: TopicEventPb => handleTopicEvent(topicEvent)
  }
}
