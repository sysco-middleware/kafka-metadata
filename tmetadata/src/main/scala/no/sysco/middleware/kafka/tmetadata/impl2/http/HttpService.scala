package no.sysco.middleware.kafka.tmetadata.impl2.http

import no.sysco.middleware.kafka.tmetadata.impl2.http.routes.{AppRoutes, HealthRoutes, MetricRoutes}
import akka.http.scaladsl.server.Directives._


trait HttpService extends AppRoutes with MetricRoutes with HealthRoutes {
  def routes = appHttpRoutes ~ healthRoutes ~ metricRoutes
}
