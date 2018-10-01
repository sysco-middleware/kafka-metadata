package no.sysco.middleware.kafka.metadata.collector.topic.internal

import java.time.Duration

import akka.actor.{Actor, ActorRef, Props}
import no.sysco.middleware.kafka.metadata.collector.proto.topic.{TopicCreatedPb, TopicDeletedPb, TopicEventPb, TopicUpdatedPb}

import scala.concurrent.ExecutionContext

object TopicManager {
  def props(pollFrequency: Duration, topicRepository: ActorRef, topicEventProducer: ActorRef): Props =
    Props(new TopicManager(pollFrequency, topicRepository, topicEventProducer))
}

class TopicManager(pollFrequency: Duration, topicRepository: ActorRef, topicEventProducer: ActorRef) extends Actor {

  implicit val executionContext: ExecutionContext = context.dispatcher

  var topicsAndDescription: Map[String, Option[Description]] = Map()

  def evaluateCurrentTopics(names: List[String]): Unit = {
    topicsAndDescription.keys.toList match {
      case Nil =>
      case name :: ns =>
        if (!names.contains(name)) topicEventProducer ! TopicEventPb(name, TopicEventPb.Event(TopicDeletedPb()))
        evaluateCurrentTopics(ns)
    }
  }

  def handleTopicsCollected(topicsCollected: TopicsCollected): Unit = {
    evaluateCurrentTopics(topicsCollected.names)
    evaluateTopicsCollected(topicsCollected.names)
  }

  def evaluateTopicsCollected(topicNames: List[String]): Unit = topicNames match {
    case Nil =>
    case name :: names =>
      name match {
        case n if !topicsAndDescription.keys.exists(_.equals(n)) =>
          topicEventProducer ! TopicEventPb(name, TopicEventPb.Event(TopicCreatedPb()))
        case n if topicsAndDescription.keys.exists(_.equals(n)) =>
          topicRepository ! DescribeTopic(name)
      }
      evaluateTopicsCollected(names)
  }

  def handleTopicEvent(topicEvent: TopicEventPb): Unit =
    topicEvent.event match {
      case event: Event if event.isTopicCreated =>
        event.topicCreated match {
          case Some(_) =>
            topicRepository ! DescribeTopic(topicEvent.name)
            topicsAndDescription = topicsAndDescription + (topicEvent.name -> None)
        }
      case event: Event if event.isTopicUpdated =>
        event.topicUpdated match {
          case Some(topicUpdated) =>
            val topicDescription = Some(Parser.fromPb(topicEvent.name, topicUpdated.topicDescription.get))
            topicsAndDescription = topicsAndDescription + (topicEvent.name -> topicDescription)
        }

      case event: Event if event.isTopicDeleted =>
        event.topicDeleted match {
          case Some(_) =>
            topicsAndDescription = topicsAndDescription - topicEvent.name
        }
    }

  def handleTopicDescribed(topicDescribed: TopicDescribed): Unit = topicDescribed.topicAndDescription match {
    case (name: String, description: Description) =>
      topicsAndDescription(name) match {
        case currentTopicDescription =>
          if (!currentTopicDescription.get.equals(description))
            topicEventProducer ! TopicEventPb(name, TopicEventPb.Event(TopicUpdatedPb()))
      }
  }

  def handleCollectTopics(): Unit = {
    topicRepository ! CollectTopics()

    context.system.scheduler.scheduleOnce(pollFrequency, () => self ! CollectTopics())
  }

  override def receive: Receive = {
    case topicsCollected: TopicsCollected => handleTopicsCollected(topicsCollected)
    case topicDescribed: TopicDescribed => handleTopicDescribed(topicDescribed)
    case topicEvent: TopicEventPb => handleTopicEvent(topicEvent)
    case CollectTopics => handleCollectTopics()
  }

}
