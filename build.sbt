// Project setup
import scalariform.formatter.preferences._
import Dependencies._

val projectV = "0.1.0-SNAPSHOT"
val scalaV = "2.12.7"

// https://www.scala-sbt.org/release/docs/Basic-Def-Examples.html
lazy val settings = Seq(
  scalaVersion := scalaV,

  test in assembly := {},

  // set the main Scala source directory to be <base>/src
  scalaSource in Compile := baseDirectory.value / "src/main/scala",

  // set the Scala test source directory to be <base>/test
  scalaSource in Test := baseDirectory.value / "src/test/scala",

  // append several options to the list of options passed to the Java compiler
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),

  // set the initial commands when entering 'console' or 'consoleQuick', but not 'consoleProject'
  initialCommands in console := "import no.sysco.middleware.kafka.metadata._",

  // only use a single thread for building
  parallelExecution := false,

  //Run tests Sequentially
  parallelExecution in Test := false

)

                                    /** projects */
lazy val root = project
  .in(file("."))
  .settings(
    name := "kafka-metadata",
    organization := "no.sysco.middleware.kafka.metadata",
    version := projectV,
    settings,
    libraryDependencies ++= commonDependencies ++ observabilityDependencies ++ testDependencies,

    mainClass in assembly := Some(s"no.sysco.middleware.kafka.metadata.topic.Main"),
    assemblyJarName in assembly := "ktm-fat-jar.jar"
  )
  .aggregate(topic)
  .enablePlugins(JavaAppPackaging)

lazy val topic = project
  .in(file("topic"))
  .settings(
    name := "kafka-metadata-topic",
    organization := "no.sysco.middleware.kafka.metadata",
    version := projectV,
    settings,
    libraryDependencies ++= commonDependencies ++ observabilityDependencies ++ testDependencies,

    mainClass in assembly := Some(s"no.sysco.middleware.kafka.metadata.topic.Main"),
    assemblyJarName in assembly := "ktm-fat-jar.jar"
  )
  .enablePlugins(JavaAppPackaging)

lazy val schema = project
  .in(file("schema"))
  .settings(
    name := "schema",
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
