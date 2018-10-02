package no.sysco.middleware.kafka.metadata.collector.topic

import java.time.Duration

import com.typesafe.config.Config

class TopicCollectorConfig(config: Config) {
  object Collector {
    val pollInteval: Duration = config.getDuration("collector.topic.poll-interval")
    val topicEventTopic: String = config.getString("collector.topic.event-topic")
  }
  object Kafka {
    val bootstrapServers: String = config.getString("kafka.bootstrap-servers")
  }
}
