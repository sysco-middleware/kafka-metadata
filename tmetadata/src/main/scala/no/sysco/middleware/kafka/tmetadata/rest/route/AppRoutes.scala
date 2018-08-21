package no.sysco.middleware.kafka.tmetadata.rest.route


import akka.actor.ActorRef
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import no.sysco.middleware.kafka.tmetadata.actors.KafkaService.{RegisterTopicMetadataCommand, ResultEvent}
import no.sysco.middleware.kafka.tmetadata.rest.{TopicMetadata, TopicVendorProtocol}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._ //For the timeout duratino "5 seconds"

class AppRoutes(kafkaServiceRef: ActorRef)(implicit executionContext: ExecutionContext) extends TopicVendorProtocol{

  implicit lazy val timeout = Timeout(5 seconds)

  private[rest] val appRoutes = path("topics") {
    get {
      pathEndOrSingleSlash{
        complete(StatusCodes.OK)
      }
    } ~
      post {
        pathEndOrSingleSlash{
          entity(as[TopicMetadata]){ json =>
            onSuccess(kafkaServiceRef ? RegisterTopicMetadataCommand(json)){
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

}

