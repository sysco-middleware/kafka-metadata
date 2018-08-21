package no.sysco.middleware.kafka.tmetadata.actors

import akka.actor.{Actor, ActorLogging, Props}


object AppSupervisor {
  def props(): Props = Props(new AppSupervisor)
}

class AppSupervisor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("Application started")
  override def postStop(): Unit = log.info("Application stopped")

  // No need to handle any messages
  override def receive = Actor.emptyBehavior
}