package no.sysco.middleware.kafka.tmetadata.rest.routes

import akka.actor.ActorRef
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern._
import akka.util.Timeout
import no.sysco.middleware.kafka.tmetadata.application.KafkaService
import no.sysco.middleware.kafka.tmetadata.application.KafkaService.{ FetchTopicsMetadata, FetchedTopicsMetadata, RegisterTopicMetadata, RegisteredTopicMetadataAttempt }
import no.sysco.middleware.kafka.tmetadata.rest.{ TopicMetadata, TopicMetadataJsonProtocol }

import scala.concurrent.duration._ //For the timeout duratino "5 seconds"

trait AppRoutes extends TopicMetadataJsonProtocol {

  def kafkaService: KafkaService

  implicit val timeout = Timeout(5 seconds)

  val appHttpRoutes: Route = path("topics") {
    get {
      pathEndOrSingleSlash {
        onSuccess(kafkaService.topicsMeta()) {
          case rez: Seq[TopicMetadata] => complete(rez)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    } ~
      post {
        entity(as[TopicMetadata]) { json =>
          println(json)
          onSuccess(kafkaService.registerTopicMeta(RegisterTopicMetadata(json))) {
            rez: RegisteredTopicMetadataAttempt =>
              if (rez.success) {
                complete(StatusCodes.OK)
              } else {
                complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, rez.message)))
              }
          }
        }
      }

  }
}
