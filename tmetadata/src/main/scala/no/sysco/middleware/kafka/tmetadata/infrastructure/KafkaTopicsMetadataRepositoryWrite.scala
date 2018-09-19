package no.sysco.middleware.kafka.tmetadata.infrastructure

import java.util.Properties

import no.sysco.middleware.kafka.tmetadata.ApplicationConfig
import no.sysco.middleware.kafka.tmetadata.application.KafkaService._
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.Try

object KafkaTopicsMetadataRepositoryWrite {

  val topic = Topics.METADATA

  def initRepository(config: ApplicationConfig)(implicit executionContext: ExecutionContext): KafkaTopicsMetadataRepositoryWrite = {
    val kafkaProducer: KafkaProducer[String, String] = new KafkaProducer(producerProps(config))
    new KafkaTopicsMetadataRepositoryWrite(kafkaProducer, topic)
  }

  def producerProps(config: ApplicationConfig): Properties = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka.bootstrapServers)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props
  }

}

class KafkaTopicsMetadataRepositoryWrite(kafkaProducer: KafkaProducer[String, String], topic: String)(implicit executionContext: ExecutionContext) {

  def registerSync(command: RegisterTopicMetadata): Future[RegisteredTopicMetadataAttempt] = {
    val record = new ProducerRecord[String, String](topic, command.json.topicName, command.json.toString)
    try {
      kafkaProducer.send(record)
      Future(RegisteredTopicMetadataAttempt())
    } catch {
      case e: Exception =>
        e.printStackTrace()
        Future(RegisteredTopicMetadataAttempt(success = false, message = e.getMessage))
    }
  }

  def registerAsync(command: RegisterTopicMetadata): Future[RegisteredTopicMetadataAttempt] = {
    val record = new ProducerRecord[String, String](topic, command.json.topicName, command.json.toString)
    val promise = Promise[RegisteredTopicMetadataAttempt]()
    kafkaProducer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        val result = if (exception == null) RegisteredTopicMetadataAttempt() else RegisteredTopicMetadataAttempt(success = false, exception.getMessage)
        promise.complete(Try(result))
      }
    })
    promise.future
  }

  def registerBatch(listCommands: Seq[RegisterTopicMetadata]) =
    listCommands.foreach(command => kafkaProducer.send(new ProducerRecord[String, String](topic, command.json.topicName, command.json.toString)))

}
