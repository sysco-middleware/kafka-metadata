package no.sysco.middleware.metadata.api.rest

import no.sysco.middleware.metadata.api.rest.routes.{AppRoutes, HealthRoutes, MetricRoutes}
import akka.http.scaladsl.server.Directives._

trait HttpRoutes extends AppRoutes with MetricRoutes with HealthRoutes {
  def routes = appHttpRoutes ~ healthRoutes ~ metricRoutes
}
