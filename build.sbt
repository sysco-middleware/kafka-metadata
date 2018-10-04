// Project setup
import scalariform.formatter.preferences._
import Dependencies._

val projectV = "0.0.1-SNAPSHOT"
val scalaV = "2.12.6"


val settings = Settings(scalaV).default

                                    /** projects */
lazy val root = project
  .in(file("."))
  .settings(
    name := "ktm",
    organization := "no.sysco.middleware.ktm",
    version := projectV,
    settings,
    libraryDependencies ++= commonDependencies ++ observabilityDependencies ++ testDependencies,

    mainClass in assembly := Some(s"no.sysco.middleware.ktm.Main"),
    assemblyJarName in assembly := "ktm-fat-jar.jar"
  )
  .aggregate(topicCollector)
  .enablePlugins(JavaAppPackaging)

lazy val topicCollector = project
  .in(file("collector/topic"))
  .settings(
    name := "metadata-collector-topic",
    organization := "no.sysco.middleware.kafka.metadata",
    libraryDependencies ++= commonDependencies ++ observabilityDependencies ++ testDependencies,
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    )
  )

lazy val schemas = project
  .in(file("schemas"))
  .settings(
    name := "metadata-schemas",
    organization := "no.sysco.middleware.kafka.metadata",
    libraryDependencies ++= commonDependencies ++ observabilityDependencies ++ testDependencies,
  )
                                      /** dependencies */
lazy val commonDependencies = Seq(
  akka_http,
  akka_streams,
  akka_http_core,
  akka_http_spray,
  kafka_clients,
  kafka_streams,
  akka_slf4j,
  akka_slf4j_logback,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.22",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
)
lazy val observabilityDependencies = Seq(
  prometheus_simple_client,
  prometheus_common,
  prometheus_hot_spot
)
lazy val testDependencies = Seq(
  scala_test,
  akka_test_kit,
  "net.manub" %% "scalatest-embedded-kafka" % "2.0.0" % "test"
)

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)
