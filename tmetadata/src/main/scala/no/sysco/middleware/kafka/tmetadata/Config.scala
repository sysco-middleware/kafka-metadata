package no.sysco.middleware.kafka.tmetadata

import com.typesafe.config.ConfigFactory

object Env extends Enumeration {
  type Env = Value
  val dev, test = Value
}

case class KafkaConfig(bootstrapServers: String)
case class RestConfig(serviceName: String, host: String, port: Int)
case class ApplicationConfig(kafka:KafkaConfig, rest:RestConfig, env: Env.Value)

object Config {

  def loadConfig():ApplicationConfig = {
    val config = ConfigFactory.load()
    val kafka = KafkaConfig(config.getString("kafka.bootstrap-servers"))
    val rest = RestConfig(
      config.getString("service.name"),
      config.getString("service.host"),
      config.getInt("service.port")
    )
    val env = Env.withName(config.getString("environment").trim.toLowerCase)
    ApplicationConfig(kafka, rest, env)
  }

}


