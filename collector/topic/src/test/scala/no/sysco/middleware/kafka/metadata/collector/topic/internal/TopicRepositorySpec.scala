package no.sysco.middleware.kafka.metadata.collector.topic.internal

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.apache.kafka.clients.admin.{MockAdminClient, TopicDescription}
import org.apache.kafka.common.{PartitionInfo, TopicPartitionInfo}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.collection.JavaConverters._

class TopicRepositorySpec extends TestKit(ActorSystem("test-topic-repository")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Topic Repository actor" must {
    "send back topics collected" in {
      val repo = system.actorOf(TopicRepository.props(MockAdminClient(List("topic"))))
      repo ! CollectTopics()
      expectMsg(TopicsCollected(List("topic")))
    }
    "send back topic Described" in {
      val node = new org.apache.kafka.common.Node(0, "localhost", 9092, null)
      val topicDescription =
        new TopicDescription(
          "topic",
          false,
          List(
            new TopicPartitionInfo(
              0,
              node,
              List(node).asJava,
              List(node).asJava)).asJava)

      val repo = system.actorOf(TopicRepository.props(
        MockAdminClient(
          topicDescription)))

      repo ! DescribeTopic("topic")
      expectMsg(TopicDescribed("topic", Parser.fromKafka(topicDescription)))
    }
  }
}
