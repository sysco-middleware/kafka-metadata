package no.sysco.middleware.kafka.metadata.collector.topic

import java.time.Duration

import com.typesafe.config.Config

class TopicCollectorConfig(config: Config) {
  object Collector {
    val pollFrequency: Duration = config.getDuration("collector.pollFrequency")
  }
  object Kafka {
    val bootstrapServers: String = config.getString("kafka.bootstrapServers")
  }
}
