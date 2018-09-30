package no.sysco.middleware.kafka.metadata.collector.topic.internal

import no.sysco.middleware.kafka.metadata.collector.proto.topic.{NodePb, TopicDescriptionPb}
import org.apache.kafka.clients.admin.TopicDescription
import org.apache.kafka.common.{Node, TopicPartitionInfo}

trait Event

trait Command

case class CollectTopics() extends Command

case class TopicsCollected(names: List[String]) extends Event

case class DescribeTopics(names: List[String]) extends Command

case class TopicsDescribed(topicsAndDescription: Map[String, TopicDescription]) extends

object Parser {

  import scala.collection.JavaConverters._

  def parsePb(name: String, topicDescriptionPb: TopicDescriptionPb): TopicDescription =
    new TopicDescription(
      name,
      topicDescriptionPb.internal,
      topicDescriptionPb.topicPartitions
        .map(tp =>
          new TopicPartitionInfo(
            tp.partition,
            parsePb(tp.leader.get),
            tp.replicas.map(rep => parsePb(rep)).asJava,
            tp.isr.map(rep => parsePb(rep)).asJava
          )).asJava)

  def parsePb(node: NodePb): Node = new Node(node.id, node.host, node.port, node.rack)
}