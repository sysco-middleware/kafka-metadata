package no.sysco.middleware.metadata.api

import com.typesafe.config.ConfigFactory

object Env extends Enumeration {
  type Env = Value
  val dev, test = Value
}

case class KafkaConfig(bootstrapServers: String, timeout: Long)
case class RestConfig(serviceName: String, host: String, port: Int)
case class ApplicationConfig(kafka: KafkaConfig, rest: RestConfig, env: Env.Value)

object AppConfig {

  def loadConfig(): ApplicationConfig = {
    val config = ConfigFactory.load("application.conf")
    println(config)
    val kafka = KafkaConfig(config.getString("kafka.bootstrap-servers"), config.getLong("kafka.timeout"))
    val rest = RestConfig(
      config.getString("service.name"),
      config.getString("service.host"),
      config.getInt("service.port"))
    val env = Env.withName(config.getString("environment").trim.toLowerCase)
    ApplicationConfig(kafka, rest, env)
  }

}

