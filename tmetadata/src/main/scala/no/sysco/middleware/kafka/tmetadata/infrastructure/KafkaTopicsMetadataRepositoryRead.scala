package no.sysco.middleware.kafka.tmetadata.infrastructure

import java.util.Properties
import java.util.concurrent.CountDownLatch

import no.sysco.middleware.kafka.tmetadata.ApplicationConfig
import no.sysco.middleware.kafka.tmetadata.rest.{TopicMetadata, TopicMetadataJsonProtocol}
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.{KeyValueIterator, KeyValueStore, QueryableStoreTypes, ReadOnlyKeyValueStore}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig, Topology}
import spray.json.JsonParser

import scala.collection.mutable.ListBuffer


object KafkaTopicsMetadataRepositoryRead {

  def initRepository(config: ApplicationConfig):KafkaTopicsMetadataRepositoryRead = {
    new KafkaTopicsMetadataRepositoryRead(config)
  }

}

class KafkaTopicsMetadataRepositoryRead(config: ApplicationConfig) extends TopicMetadataJsonProtocol {
  val topic = Topics.METADATA
  val storageName = Topics.METADATA_STORAGE

  val builder = new StreamsBuilder
  val topology = buildTopology(builder)
  val streams: KafkaStreams = new KafkaStreams(topology, getProps())

  def getProps(): Properties = {
    val props = new Properties
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka.bootstrapServers)
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-client-id")
    props
  }

  def buildTopology(builder: StreamsBuilder): Topology = {
    val tMetastorage = builder.table(
        topic,
        Materialized.as[String, String, KeyValueStore[Bytes, Array[Byte]]](storageName))
    builder.build()
  }


  def topicsMetadata(): Seq[TopicMetadata] = {
    val storeType = QueryableStoreTypes.keyValueStore[String, String]()
    val keyValueStore: ReadOnlyKeyValueStore[String, String] = streams.store(storageName, storeType)
    val it: KeyValueIterator[String, String] = keyValueStore.all()
    var list = new ListBuffer[TopicMetadata]()

    while (it.hasNext) {
      val nextKV = it.next
      list += JsonParser(nextKV.value).convertTo[TopicMetadata]
    }

    list
  }

  def addShutdownHook(latch: CountDownLatch, streamAppId: String): Unit = {
    // attach shutdown handler to catch control-c
    Runtime.getRuntime.addShutdownHook(new Thread(s"$streamAppId-shutdown-hook") {
      override def run(): Unit = {
        streams.close()
        latch.countDown()
      }
    })

    try {
      streams.start()
      latch.await()
    } catch {
      case e: Throwable =>
        System.exit(1)
    }
    System.exit(0)
  }


}


