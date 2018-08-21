package no.sysco.middleware.kafka.tmetadata.rest

import no.sysco.middleware.kafka.tmetadata.rest.routes.{AppRoutes, HealthRoutes, MetricRoutes}
import akka.http.scaladsl.server.Directives._

trait HttpService extends AppRoutes with MetricRoutes with HealthRoutes {
  def routes = appHttpRoutes ~ healthRoutes ~ metricRoutes
}
