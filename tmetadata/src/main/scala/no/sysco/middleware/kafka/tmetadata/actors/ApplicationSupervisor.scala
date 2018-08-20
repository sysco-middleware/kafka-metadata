package no.sysco.middleware.kafka.tmetadata.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.server.Route
import no.sysco.middleware.kafka.tmetadata.ApplicationConfig
import no.sysco.middleware.kafka.tmetadata.rest.HttpRoutes

import scala.concurrent.ExecutionContext


final case class RouteRequest()
final case class RouteResponse(route: Route)


object ApplicationSupervisor {
  def props(config: ApplicationConfig)(implicit executionContext: ExecutionContext) : Props = Props(new ApplicationSupervisor(config))
}

class ApplicationSupervisor(config: ApplicationConfig)(implicit executionContext: ExecutionContext) extends Actor with ActorLogging {

  val kafkaService = context.actorOf(KafkaService.props(config), "kafka-service-actor")
  val routes: Route = new HttpRoutes(kafkaService).routes


  override def preStart(): Unit = {
    println("Application supervisor starts")
    super.preStart()
  }


  override def receive: Receive = {
    case RouteRequest => sender() ! routes
    case _ => println(s"All ok")
  }

}
