package no.sysco.middleware.kafka.tmetadata

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object Boot extends App {

  // config
  val config = ConfigHolder.loadConfig()

  // actor system
  implicit val system = ActorSystem("kafka-management-operations")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  // app
  val supervisor = system.actorOf(ApplicationSupervisor.props(config), "tmetadata-app-supervisor")

}
