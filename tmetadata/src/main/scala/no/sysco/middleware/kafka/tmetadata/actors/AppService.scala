package no.sysco.middleware.kafka.tmetadata.actors

import akka.actor.{Actor, ActorLogging, Props}

/**
  * This actor service works as a application supervisor
  * */
object AppService {
  def props(): Props = Props(new AppService)
}

class AppService extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("Application started")
  override def postStop(): Unit = log.info("Application stopped")

  // No need to handle any messages
  override def receive = Actor.emptyBehavior
}