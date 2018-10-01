package no.sysco.middleware.kafka.metadata.collector.topic.internal

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class TopicEventConsumerSpec
  extends TestKit(ActorSystem("test-topic-event-consumer"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with EmbeddedKafka {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val kafkaConfig: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = 0, zooKeeperPort = 0)


  "Topic Event Producer" must {
    "send Topic Events to a Kafka Topic" in {
      withRunningKafkaOnFoundPort(kafkaConfig) { implicit actualConfig =>
        val bootstrapServers = s"localhost:${actualConfig.kafkaPort}"

      }
    }
  }
}
