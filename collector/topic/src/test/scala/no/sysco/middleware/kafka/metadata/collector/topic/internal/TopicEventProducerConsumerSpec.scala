package no.sysco.middleware.kafka.metadata.collector.topic.internal

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.{ ImplicitSender, TestKit, TestProbe }
import net.manub.embeddedkafka.{ EmbeddedKafka, EmbeddedKafkaConfig }
import no.sysco.middleware.kafka.metadata.collector.proto.topic.{ TopicCreatedPb, TopicEventPb }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

class TopicEventProducerConsumerSpec
  extends TestKit(ActorSystem("test-topic-event-producer-consumer"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with EmbeddedKafka {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val kafkaConfig: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = 0, zooKeeperPort = 0)

  "Topic Event Producer and Consumer" must {
    "send and receive Topic Events from/to a Kafka Topic" in {
      withRunningKafkaOnFoundPort(kafkaConfig) { implicit actualConfig =>
        implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
        implicit val executionContext: ExecutionContext = system.dispatcher
        val probe = TestProbe()
        val bootstrapServers = s"localhost:${actualConfig.kafkaPort}"
        val topicEventTopic = "__topic"

        system.actorOf(TopicEventConsumer.props(probe.ref, bootstrapServers, topicEventTopic))

        val eventProducer = system.actorOf(TopicEventProducer.props(bootstrapServers, topicEventTopic))

        val topicEvent = TopicEventPb("test", TopicEventPb.Event.TopicCreated(TopicCreatedPb()))
        eventProducer ! topicEvent

        val topicEventReceived = probe.expectMsgType[TopicEventPb](10 seconds)
        assert(topicEvent.equals(topicEventReceived))
      }
    }
  }
}
