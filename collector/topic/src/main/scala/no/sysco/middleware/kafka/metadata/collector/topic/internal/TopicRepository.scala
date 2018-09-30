package no.sysco.middleware.kafka.metadata.collector.topic.internal

import akka.actor.{ Actor, Props }
import org.apache.kafka.clients.admin.AdminClient

import scala.collection.JavaConverters._

object TopicRepository {
  def props(adminClient: AdminClient): Props = Props(new TopicRepository(adminClient))
}

class TopicRepository(adminClient: AdminClient) extends Actor {

  def handleCollectTopics(): Unit = {
    adminClient.listTopics()
      .names()
      .thenApply(names => sender() ! TopicsCollected(names.asScala.toList))
  }

  def handleDescribeTopics(describeTopics: DescribeTopics): Unit = {
    adminClient.describeTopics(describeTopics.names.asJava)
      .all()
      .thenApply(topicsAndDescription => {
        sender() ! TopicsDescribed(topicsAndDescription.asScala.toMap)
      })
  }

  override def receive: Receive = {
    case CollectTopics => handleCollectTopics()
    case describeTopics: DescribeTopics => handleDescribeTopics(describeTopics)
  }

}
