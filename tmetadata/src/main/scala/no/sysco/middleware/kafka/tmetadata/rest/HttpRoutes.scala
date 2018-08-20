package no.sysco.middleware.kafka.tmetadata.rest

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import no.sysco.middleware.kafka.tmetadata.rest.route.AppRoutes
import no.sysco.middleware.kafka.tmetadata.rest.route.HealthRoutes._
import no.sysco.middleware.kafka.tmetadata.rest.route.MetricsRoutes._

import scala.concurrent.ExecutionContext



class HttpRoutes(val kafkaService: ActorRef)(implicit executionContext: ExecutionContext) {
  val routes: Route = healthRoute ~ metricRoute ~ new AppRoutes(kafkaService).appRoutes
}
