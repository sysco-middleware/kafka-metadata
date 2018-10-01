package no.sysco.middleware.kafka.metadata.collector.topic.internal

import no.sysco.middleware.kafka.metadata.collector.proto.topic.TopicDescriptionPb.TopicPartitionInfoPb
import no.sysco.middleware.kafka.metadata.collector.proto.topic.{NodePb, TopicDescriptionPb, TopicUpdatedPb}
import org.apache.kafka.clients.admin.TopicDescription

trait Event

trait Command

case class CollectTopics() extends Command

case class TopicsCollected(names: List[String]) extends Event

case class DescribeTopic(name: String) extends Command

case class TopicDescribed(topicAndDescription: (String, Description)) extends

case class Description(internal: Boolean, partitions: Seq[Partition])

case class Partition(id: Int, replicas: Seq[Node])

case class Node(id: Int, host: String, port: Int, rack: String)

object Parser {
  import scala.collection.JavaConverters._

  def fromKafka(description: TopicDescription): Description =
    Description(
      description.isInternal,
      description.partitions().asScala
        .map(partition =>
          Partition(
            partition.partition(),
            partition.replicas().asScala
              .map(node =>
                Node(node.id(), node.host(), node.port(), node.rack())))))


  def fromPb(name: String, topicDescriptionPb: TopicDescriptionPb): Description =
    new Description(
      topicDescriptionPb.internal,
      topicDescriptionPb.topicPartitions
        .map(tp =>
          Partition(
            tp.partition,
            tp.replicas.map(rep => fromPb(rep))
          )))

  def fromPb(node: NodePb): Node = Node(node.id, node.host, node.port, node.rack)

  def toPb(topicDescription: Description): TopicUpdatedPb =
    TopicUpdatedPb(
      Some(
        TopicDescriptionPb(
          topicDescription.internal,
          topicDescription.partitions
            .map(tpi => TopicPartitionInfoPb(tpi.id, tpi.replicas.map(node => toPb(node)))))))

  def toPb(node: Node): NodePb = NodePb(node.id, node.host, node.port, node.rack)
}