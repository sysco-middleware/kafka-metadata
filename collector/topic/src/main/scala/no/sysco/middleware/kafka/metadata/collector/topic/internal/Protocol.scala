package no.sysco.middleware.kafka.metadata.collector.topic.internal

trait State

trait Event

trait Command

case class CollectTopics() extends Command

case class TopicsCollected(names: List[String]) extends Event

case class DescribeTopic(name: String) extends Command

case class TopicDescribed(topicAndDescription: (String, Description)) extends State

case class Description(internal: Boolean, partitions: Seq[Partition]) extends State

case class Partition(id: Int, replicas: Seq[Node]) extends State

case class Node(id: Int, host: String, port: Int, rack: String) extends State
