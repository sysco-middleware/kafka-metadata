package no.sysco.middleware.kafka.tmetadata.infrastructure

import java.util.Properties

import no.sysco.middleware.kafka.tmetadata.ApplicationConfig
import no.sysco.middleware.kafka.tmetadata.actors.KafkaService.{RegisterTopicMetadataCommand, ResultEvent}

import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

object KafkaRepository {

  val topic = "topics-metadata"

  def initRepository(config: ApplicationConfig)(implicit executionContext: ExecutionContext):KafkaRepository = {
    val kafkaProducer: KafkaProducer[String, String] = new KafkaProducer(producerProps(config))
    new KafkaRepository(kafkaProducer, topic)
  }
  def producerProps(config: ApplicationConfig):Properties = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka.bootstrapServers)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props
  }

}

class KafkaRepository(kafkaProducer: KafkaProducer[String, String], topic: String)(implicit executionContext: ExecutionContext) {

  def registerSync(command: RegisterTopicMetadataCommand): ResultEvent = {
    val record = new ProducerRecord[String, String](topic, command.json.topicName, command.json.toString)
    try {
      kafkaProducer.send(record)
      ResultEvent()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        ResultEvent(success = false, message = e.getMessage)
    }
  }

  def registerAsync(command: RegisterTopicMetadataCommand): Future[ResultEvent] = {
    val record = new ProducerRecord[String, String](topic, command.json.topicName, command.json.toString)
    val promise = Promise[ResultEvent]()
    kafkaProducer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        val result = if (exception == null) ResultEvent() else ResultEvent(success = false, exception.getMessage)
        promise.complete(Try(result))
      }
    })
    promise.future
  }

}
