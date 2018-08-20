package no.sysco.middleware.kafka.tmetadata.rest.route

import java.io.{StringWriter, Writer}

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.{complete, get, path, pathEndOrSingleSlash}
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

object MetricsRoutes {

  private[rest] val metricRoute = path("metrics") {
    pathEndOrSingleSlash{
      get {
        val writer: Writer = new StringWriter()
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples())
        complete(HttpEntity(writer.toString))
      }
    }
  }

}
