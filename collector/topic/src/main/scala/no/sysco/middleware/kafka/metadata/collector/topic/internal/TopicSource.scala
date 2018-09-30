package no.sysco.middleware.kafka.metadata.collector.topic.internal

import java.time.Duration

import akka.actor.{ Actor, ActorRef }

import scala.concurrent.ExecutionContext

class TopicSource(pollFrequency: Duration) extends Actor {
  implicit val executionContext: ExecutionContext = context.dispatcher

  val topicRepository: ActorRef = context.actorOf(TopicRepository.props(null))
  val topicManager: ActorRef = context.actorOf(TopicManager.props(topicRepository))

  def handleCollectTopics(): Unit = {
    topicRepository.tell(CollectTopics(), topicManager)

    context.system.scheduler.scheduleOnce(pollFrequency, () => self ! CollectTopics())
  }

  override def receive: Receive = {
    case CollectTopics => handleCollectTopics()
  }
}
