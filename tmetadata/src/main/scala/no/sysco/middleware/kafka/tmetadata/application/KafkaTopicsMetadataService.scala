package no.sysco.middleware.kafka.tmetadata.application


import java.util.concurrent.CountDownLatch

import no.sysco.middleware.kafka.tmetadata.application.KafkaService.{RegisterTopicMetadata, RegisteredTopicMetadataAttempt}
import no.sysco.middleware.kafka.tmetadata.infrastructure.{KafkaTopicsMetadataRepositoryRead, KafkaTopicsMetadataRepositoryWrite}
import no.sysco.middleware.kafka.tmetadata.rest.TopicMetadata
import no.sysco.middleware.kafka.tmetadata.{ApplicationConfig, Env}

import scala.concurrent.{ExecutionContext, Future}



trait KafkaService{
  def registerTopicMeta(command: RegisterTopicMetadata):Future[RegisteredTopicMetadataAttempt]

}

object KafkaService {
  sealed trait Command
  sealed trait Event

  final case class RegisterTopicMetadata(json: TopicMetadata) extends Command
  final case class RegisteredTopicMetadataAttempt(success: Boolean = true, message: String = "") extends Event

  final case class FetchTopicsMetadata() extends Command
  final case class FetchedTopicsMetadata(topicsMetadata : Seq[TopicMetadata]) extends Event
}

class KafkaTopicsMetadataService(config: ApplicationConfig)(implicit executionContext: ExecutionContext) extends KafkaService {

  val kafkaRepositoryWrite = KafkaTopicsMetadataRepositoryWrite.initRepository(config)
  val kafkaRepositoryRead = KafkaTopicsMetadataRepositoryRead.initRepository(config)

  override def registerTopicMeta(command: RegisterTopicMetadata): Future[RegisteredTopicMetadataAttempt] = kafkaRepositoryWrite.registerSync(command)
}

class MockService(config: ApplicationConfig)(implicit executionContext: ExecutionContext) extends KafkaService {
  override def registerTopicMeta(command: RegisterTopicMetadata): Future[RegisteredTopicMetadataAttempt] = Future(RegisteredTopicMetadataAttempt()) // mock
}
