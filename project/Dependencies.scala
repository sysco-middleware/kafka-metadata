import sbt._

object Versions {
  val kafka         = "1.0.0"
  val akka_http      = "10.1.3"
  val akka_streams   = "2.5.14"
  val scala_test     = "3.0.4"

  // observability (Logs, Metrics, Tracing)
  val prometheus    = "0.5.0"

  // logger
  val log4j_over_slf4j = "1.7.12"
}

object Dependencies {
  val akka_http                 = "com.typesafe.akka"   %% "akka-http"              % Versions.akka_http
  val akka_http_core            = "com.typesafe.akka"   %% "akka-http-core"         % Versions.akka_http
  val akka_http_spray           = "com.typesafe.akka"   %% "akka-http-spray-json"   % Versions.akka_http
  val akka_streams              = "com.typesafe.akka"   %% "akka-stream"            % Versions.akka_streams

  val log4j_over_slf4j          = "org.slf4j"           % "log4j-over-slf4j"        % Versions.log4j_over_slf4j
  val akka_slf4j                = "com.typesafe.akka"   %% "akka-slf4j"             % Versions.akka_streams


  val kafka_clients             = "org.apache.kafka"    % "kafka-clients"           % Versions.kafka
  val kafka_streams             = "org.apache.kafka"    % "kafka-streams"           % Versions.kafka


  val prometheus_simple_client  = "io.prometheus"       % "simpleclient"            % Versions.prometheus
  val prometheus_common         = "io.prometheus"       % "simpleclient_common"     % Versions.prometheus
  val prometheus_hot_spot       = "io.prometheus"       % "simpleclient_hotspot"    % Versions.prometheus

  // test dependencies
  val scala_test                = "org.scalatest"       %% "scalatest"              % Versions.scala_test   % Test
}

