package no.sysco.middleware.kafka.tmetadata.rest.route

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path, pathEndOrSingleSlash}

object HealthRoutes {
  private[rest] val healthRoute = path("health"){
    pathEndOrSingleSlash{
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "OK"))
      }
    }
  }
}
