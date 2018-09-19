package no.sysco.middleware.ktm.rest.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path, pathEndOrSingleSlash}

trait HealthRoutes {

  val healthRoutes = path("health") {
    pathEndOrSingleSlash {
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "OK"))
      }
    }
  }

}
