package no.sysco.middleware.kafka.metadata.collector.topic

import java.time.Duration

import com.typesafe.config.Config

class TopicCollectorConfig(config: Config) {
  object Collector {
    val pollFrequency: Duration = config.getDuration("collector.topic.pollFrequency")
    val topicEventTopic: String = config.getString("collector.topic.eventTopic")
  }
  object Kafka {
    val bootstrapServers: String = config.getString("kafka.bootstrapServers")
  }
}
