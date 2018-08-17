package no.sysco.middleware.kafka.tmetadata

import com.typesafe.config.ConfigFactory

case class KafkaConfig(bootstrapServers: String)
case class RestConfig(serviceName: String, host: String, port: Int)
case class ApplicationConfig(kafka:KafkaConfig, rest:RestConfig)

object ConfigHolder {

  def loadConfig():ApplicationConfig = {
    val config = ConfigFactory.load()
    val kafka = KafkaConfig(config.getString("kafka.bootstrap-servers"))
    val rest = RestConfig(
      config.getString("service.name"),
      config.getString("service.host"),
      config.getInt("service.port")
    )
    ApplicationConfig(kafka, rest)
  }

}


