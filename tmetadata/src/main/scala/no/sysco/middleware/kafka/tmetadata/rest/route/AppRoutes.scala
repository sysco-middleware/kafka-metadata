package no.sysco.middleware.kafka.tmetadata.rest.route


import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import no.sysco.middleware.kafka.tmetadata.actors.RegisterTopicMetadataCommand
import no.sysco.middleware.kafka.tmetadata.rest.{TopicMetadata, TopicVendorProtocol}

class AppRoutes(kafkaService: ActorRef) extends TopicVendorProtocol{

  private[rest] val appRoutes = path("topics") {
    get {
      pathEndOrSingleSlash{
        complete(StatusCodes.OK)
      }
    } ~
    post {
      pathEndOrSingleSlash{
        entity(as[TopicMetadata]){ json =>
          println(RegisterTopicMetadataCommand(json))
          complete(StatusCodes.OK)
        }
      }
    }

  }
}
