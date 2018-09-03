package no.sysco.middleware.kafka.tmetadata.infrastructure

import java.util.{Properties, UUID}

import no.sysco.middleware.kafka.tmetadata.{ApplicationConfig, Config}
import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, ListTopicsOptions}
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.apache.kafka.common.{KafkaFuture, Node}
import org.apache.kafka.common.config.{ConfigResource, TopicConfig}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import collection.JavaConverters._
import scala.concurrent.ExecutionContext

// todo: remove after test
object KafkaRepositoryRead {

  val topic = Topics.METADATA

  def initRepository(config: ApplicationConfig):KafkaRepositoryRead = {
    val kafkaConsumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](consumerProps(config))
    new KafkaRepositoryRead(kafkaConsumer)
  }
  def consumerProps(config: ApplicationConfig):Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka.bootstrapServers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "some-id")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
    props
  }

}

class KafkaRepositoryRead(val kafkaConsumer: KafkaConsumer[String, String]) {

  def topicsList() = {
    kafkaConsumer.listTopics().keySet()
  }

}


object At extends App {
  val conf = Config.loadConfig()
  val consumer = KafkaRepositoryRead.initRepository(conf)
  println(consumer.topicsList())
}

object KafkaAdminClientDemo {
  def main(args: Array[String]): Unit = {
    val props = new Properties()
    props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")

    val adminClient = AdminClient.create(props)

    // ACLs
//    val newAcl = new AclBinding(new Resource(ResourceType.TOPIC, "my-secure-topic"), new AccessControlEntry("my-user", "*", AclOperation.WRITE, AclPermissionType.ALLOW))
//    adminClient.createAcls(List(newAcl).asJavaCollection)
    // similarly
//    adminClient.deleteAcls(???)
//    adminClient.describeAcls(???)

    // TOPICS
//    val numPartitions = 6
//    val replicationFactor = 3.toShort
//    val newTopic = new NewTopic("new-topic-name", numPartitions, replicationFactor)
//    val configs = Map(TopicConfig.CLEANUP_POLICY_CONFIG -> TopicConfig.CLEANUP_POLICY_COMPACT,
//      TopicConfig.COMPRESSION_TYPE_CONFIG -> "gzip")
    // settings some configs
//    newTopic.configs(configs.asJava)
//    adminClient.createTopics(List(newTopic).asJavaCollection)
    // similarly
//    adminClient.deleteTopics(topicNames, options)
//    adminClient.describeTopics(topicNames, options)
    val list = adminClient.listTopics(new ListTopicsOptions().timeoutMs(500).listInternal(true))
    val topics = list.listings().get()
    val topicNames = list.names().get()
//    topics.forEach(println(_))
    val described = adminClient.describeTopics(topicNames)
    println(described.all().get().values().forEach(println(_)))
    // describe topic configs
//    adminClient.describeConfigs(List(new ConfigResource(ConfigResource.Type.TOPIC, TopicConfig.CLEANUP_POLICY_CONFIG)).asJavaCollection)
//    adminClient.alterConfigs(???)


    // get Kafka configs -> make your topic respect your cluster defaults
    // get Kafka configs -> be crazy
//    adminClient.describeConfigs(List(new ConfigResource(ConfigResource.Type.BROKER, "default.replication.factor")).asJavaCollection)
//    adminClient.alterConfigs()


    // CLUSTER stuff
//    val cluster = adminClient.describeCluster()
//    val clusterId: KafkaFuture[String] = cluster.clusterId()
//    val controller: KafkaFuture[Node] = cluster.controller()
//    val nodes: KafkaFuture[util.Collection[Node]] = cluster.nodes()
    // nodes info
//    Node.noNode().host()
//    Node.noNode().id()
//    Node.noNode().port()
//    Node.noNode().rack()
  }
}