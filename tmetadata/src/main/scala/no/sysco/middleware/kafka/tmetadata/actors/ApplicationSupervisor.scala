package no.sysco.middleware.kafka.tmetadata.actors

import akka.actor.{Actor, ActorLogging, Props}
import no.sysco.middleware.kafka.tmetadata.ApplicationConfig

object ApplicationSupervisor {
  def props(config: ApplicationConfig) : Props = Props(new ApplicationSupervisor(config))
}

class ApplicationSupervisor(config: ApplicationConfig) extends Actor with ActorLogging {

  val rest = context.actorOf(RestActor.props(config), "rest-app-actor")

  override def receive: Receive = {
    case _ => println(s"All ok")
  }

}
