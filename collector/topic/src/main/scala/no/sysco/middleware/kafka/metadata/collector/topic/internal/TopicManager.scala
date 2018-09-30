package no.sysco.middleware.kafka.metadata.collector.topic.internal

import akka.actor.{Actor, ActorRef, Props}
import no.sysco.middleware.kafka.metadata.collector.proto.topic.TopicEventPb
import org.apache.kafka.clients.admin.TopicDescription

object TopicManager {
  def props(topicRepository: ActorRef): Props = Props(new TopicManager(topicRepository))
}

class TopicManager(topicRepository: ActorRef) extends Actor {

  var topicsAndDescription: Map[String, Option[TopicDescription]] = Map()

  def handleTopicsCollected(topicsCollected: TopicsCollected): Unit = {
    topicsCollected.names match {
      case Nil =>
      case (name :: names) if topicsAndDescription.keys.exists(_.equals(name)) =>
    }
  }

  def evaluateTopicsCollected(topicNames: List[String]): Unit = topicNames match {
    case Nil =>
    case name :: names =>
      name match {
        case n if !topicsAndDescription.keys.exists(_.equals(n)) =>
        case n if topicsAndDescription.keys.exists(_.equals(n)) =>
      }
  }

  def handleTopicEvent(topicEvent: TopicEventPb): Unit =
    topicEvent.event match {
      case event: Event if event.isTopicCreated =>
        event.topicCreated match {
          case Some(topicCreated) =>
            topicRepository ! DescribeTopics(List(topicEvent.name))
            topicsAndDescription = topicsAndDescription + (topicEvent.name -> None)
        }
      case event: Event if event.isTopicUpdated =>
        event.topicUpdated match {
          case Some(topicUpdated) =>
            val topicDescription = Some(Parser.parsePb(topicEvent.name, topicUpdated.topicDescription.get))
            topicsAndDescription = topicsAndDescription + (topicEvent.name -> topicDescription)
        }

      case event: Event if event.isTopicDeleted =>
        event.topicDeleted match {
          case Some(topicDeleted) =>
            topicsAndDescription = topicsAndDescription - topicEvent.name
        }
    }

  def handleTopicsDescribed(topicsDescribed: TopicsDescribed): Unit =
    evaluateTopicAndDescription(topicsDescribed.topicsAndDescription.toList)

  def evaluateTopicAndDescription(topicsAndDescription: List[(String, TopicDescription)]) =
    topicsAndDescription match {
      case (name, topicDescription) :: ts =>
        this.topicsAndDescription.get(name) match {
          case Some(t) => ??? //if (!t.equals(topicDescription))
        }
    }

  override def receive: Receive = {
    case topicsCollected: TopicsCollected => handleTopicsCollected(topicsCollected)
    case topicsDescribed: TopicsDescribed => handleTopicsDescribed(topicsDescribed)
    case topicEvent: TopicEventPb => handleTopicEvent(topicEvent)
  }

}
