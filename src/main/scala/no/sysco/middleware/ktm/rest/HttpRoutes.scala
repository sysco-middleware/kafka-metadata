package no.sysco.middleware.ktm.rest


import akka.http.scaladsl.server.Directives._
import no.sysco.middleware.ktm.rest.routes.{AppRoutes, HealthRoutes, MetricRoutes}

trait HttpRoutes extends AppRoutes with MetricRoutes with HealthRoutes {
  def routes = appHttpRoutes ~ healthRoutes ~ metricRoutes
}
