package no.sysco.middleware.kafka.tmetadata.actors

import akka.actor.{Actor, Props}
import no.sysco.middleware.kafka.tmetadata.ApplicationConfig

object RestActor {
  def props(config: ApplicationConfig) : Props = Props(new RestActor(config))
}

class RestActor(config: ApplicationConfig) extends Actor {

  override def receive: Receive = ???

}
