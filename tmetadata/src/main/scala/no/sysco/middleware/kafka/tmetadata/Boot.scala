package no.sysco.middleware.kafka.tmetadata

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.pattern.Patterns
import akka.stream.ActorMaterializer
import no.sysco.middleware.kafka.tmetadata.actors.{ApplicationSupervisor, RouteRequest, RouteResponse}

import scala.concurrent.Future

object Boot extends App {

  // config
  val config = ConfigHolder.loadConfig()

  // actor system
  implicit val system = ActorSystem("kafka-management-operations")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  // app
  val supervisor = system.actorOf(ApplicationSupervisor.props(config), "tmetadata-app-supervisor")

  //todo: how to get routes
  // 1. Get supervised ref
  // 2. Define routes separately with kafkaService

//  val asked= Patterns.ask(supervisor, RouteRequest, 2000)
//  val result: Future[RouteResponse] =
//  val r = supervisor ! RouteRequest


//  val bindingFuture = Http().bindAndHandle(supervisor., config.http.host, config.http.port)

}
