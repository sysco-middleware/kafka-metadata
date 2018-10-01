package no.sysco.middleware.kafka.metadata.collector.topic.internal

import java.util.Properties

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import no.sysco.middleware.kafka.metadata.collector.proto.topic.{TopicCreatedPb, TopicEventPb}
import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, NewTopic}
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.collection.JavaConverters._

class TopicEventProducerSpec
  extends TestKit(ActorSystem("test-topic-repository"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with EmbeddedKafka {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  implicit val materializer: ActorMaterializer = ActorMaterializer() ;

  val kafkaConfig: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = 0, zooKeeperPort = 0);


  "Topic Event Producer" must {
    "send Topic Events to a Kafka Topic" in {
      withRunningKafkaOnFoundPort(kafkaConfig) { implicit actualConfig =>
        val bootstrapServers = s"localhost:${actualConfig.kafkaPort}"

        val eventProducer = system.actorOf(TopicEventProducer.props("__topic", bootstrapServers))

        val topicEvent = TopicEventPb("test", TopicEventPb.Event.TopicCreated(TopicCreatedPb()))
        eventProducer ! topicEvent

        val message = consumeFirstMessageFrom("__topic")(actualConfig, valueDeserializer = new ByteArrayDeserializer())
        assert(topicEvent.equals(TopicEventPb.parseFrom(message)))
      }
    }
  }
}
