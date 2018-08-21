package no.sysco.middleware.kafka.tmetadata.infrastructure

import java.util.Properties

import no.sysco.middleware.kafka.tmetadata.ApplicationConfig
import no.sysco.middleware.kafka.tmetadata.rest.{TopicMetadata, TopicVendorProtocol}
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.{KeyValueIterator, KeyValueStore, QueryableStoreTypes, ReadOnlyKeyValueStore}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, Topology}
import spray.json.JsonParser

import scala.collection.mutable.ListBuffer


class KafkaTopicsMetadataRepositoryRead(config: ApplicationConfig) extends TopicVendorProtocol {
  val topic = Topics.METADATA
  val storageName = Topics.METADATA_STORAGE

  val builder = new StreamsBuilder
  val topology = buildTopology(builder)
  val streams: KafkaStreams = new KafkaStreams(topology, getProps())

  def getProps(): Properties = {
    val props = new Properties
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka.bootstrapServers)
    props
  }

  def buildTopology(builder: StreamsBuilder): Topology = {
    val tMetastorage = builder.table(
        topic,
        Materialized.as[String, String, KeyValueStore[Bytes, Array[Byte]]](storageName))
    builder.build()
  }


  def topicsMetadata(): List[TopicMetadata] = {
    val storeType = QueryableStoreTypes.keyValueStore[String, String]()
    val keyValueStore: ReadOnlyKeyValueStore[String, String] = streams.store(storageName, storeType)
    val it: KeyValueIterator[String, String] = keyValueStore.all()
    var list = new ListBuffer[TopicMetadata]()

    while (it.hasNext) {
      val nextKV = it.next
      list += JsonParser(nextKV.value).convertTo[TopicMetadata]
    }

    list.toList
  }

}


