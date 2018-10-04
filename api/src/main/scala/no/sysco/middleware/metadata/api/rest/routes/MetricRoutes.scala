package no.sysco.middleware.metadata.api.rest.routes

import java.io.{ StringWriter, Writer }

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.{ complete, get, path, pathEndOrSingleSlash }
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

trait MetricRoutes {

  val metricRoutes = path("metrics") {
    pathEndOrSingleSlash {
      get {
        val writer: Writer = new StringWriter()
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples())
        complete(HttpEntity(writer.toString))
      }
    }
  }

}
