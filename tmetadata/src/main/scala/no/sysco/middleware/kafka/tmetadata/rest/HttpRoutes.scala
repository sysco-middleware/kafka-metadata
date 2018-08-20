package no.sysco.middleware.kafka.tmetadata.rest

import java.io.{StringWriter, Writer}

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import no.sysco.middleware.kafka.tmetadata.actors.KafkaService

import scala.concurrent.ExecutionContext



class HttpRoutes(kafkaService: KafkaService)(implicit executionContext: ExecutionContext) {

  private val metricRoute = path("metrics") {
    pathEndOrSingleSlash{
      get {
        val writer: Writer = new StringWriter()
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples())
        complete(HttpEntity(writer.toString))
      }
    }
  }

  private val healthRoute = path("health"){
    pathEndOrSingleSlash{
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "OK"))
      }
    }
  }

  val routes: Route = healthRoute ~ metricRoute
}
