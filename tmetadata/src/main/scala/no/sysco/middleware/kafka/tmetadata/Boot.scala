package no.sysco.middleware.kafka.tmetadata

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import no.sysco.middleware.kafka.tmetadata.actors.KafkaService
import no.sysco.middleware.kafka.tmetadata.rest.HttpRoutes
//import no.sysco.middleware.kafka.tmetadata.actors.{ApplicationSupervisor, RouteRequest, RouteResponse}

object Boot extends App {

  // config
  val config = Config.loadConfig()

  // actor system
  implicit val system = ActorSystem("kafka-management-operations")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher


  val kafkaService = system.actorOf(KafkaService.props(config), "kafka-service-actor")
  val routes = new HttpRoutes(kafkaService).routes


  val bindingFuture = Http().bindAndHandle(routes, config.rest.host, config.rest.port)


}
