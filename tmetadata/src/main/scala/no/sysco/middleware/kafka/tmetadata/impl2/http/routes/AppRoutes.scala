package no.sysco.middleware.kafka.tmetadata.impl2.http.routes

import akka.actor.ActorRef
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{as, complete, entity, get, path, pathEndOrSingleSlash, post, _}
import akka.http.scaladsl.server.Route
import akka.pattern._
import akka.util.Timeout
import no.sysco.middleware.kafka.tmetadata.actors.KafkaService.{RegisterTopicMetadataCommand, ResultEvent}
import no.sysco.middleware.kafka.tmetadata.rest.{TopicMetadata, TopicVendorProtocol}

import scala.concurrent.duration._ //For the timeout duratino "5 seconds"

trait AppRoutes extends TopicVendorProtocol {

  def log: LoggingAdapter
  def kafkaService: ActorRef

  implicit val timeout = Timeout(5 seconds)

  val appHttpRoutes: Route = path("topics") {
    get {
      pathEndOrSingleSlash {
        complete(StatusCodes.OK)
      }
    } ~
      post {
        entity(as[TopicMetadata]) { json =>
          onSuccess(kafkaService ? RegisterTopicMetadataCommand(json)){
            case rez: ResultEvent => if (rez.success) {
              complete(StatusCodes.OK)
            } else {
              complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, rez.message)))
            }
          }
        }
      }

  }
}
