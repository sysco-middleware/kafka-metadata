// Project setup
import Dependencies._

val rootPackage = "no.sysco.middleware"
val subRootPackage = s"$rootPackage.kafka"
val projectV = "0.0.1-SNAPSHOT"
val scalaV = "2.12.6"

// https://www.scala-sbt.org/release/docs/Basic-Def-Examples.html
lazy val settings = Seq(

  organization := s"$subRootPackage",
  version := projectV,
  scalaVersion := scalaV,

  test in assembly := {},

  // set the main Scala source directory to be <base>/src
  scalaSource in Compile := baseDirectory.value / "src/main/scala",

  // set the Scala test source directory to be <base>/test
  scalaSource in Test := baseDirectory.value / "src/test/scala",

  // append several options to the list of options passed to the Java compiler
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),

  // set the initial commands when entering 'console' or 'consoleQuick', but not 'consoleProject'
  initialCommands in console := "import no.sysco.middleware.kafka._",

  // only use a single thread for building
  parallelExecution := false,

  //Run tests Sequentially
  parallelExecution in Test := false

)

                                    /** projects */
lazy val root = project
  .in(file("."))
  .settings(
    name := "kafka",
    organization := rootPackage,
    version := projectV
  )
  .aggregate(
    tmetadata
  )

lazy val tmetadata = project
  .settings(
    name := "tmetadata",
    settings,
    libraryDependencies ++= commonDependencies ++ observabilityDependencies ++ testDependencies,

    mainClass in assembly := Some(s"$subRootPackage.tmetadata.Boot"),
    assemblyJarName in assembly := "tmetadata-fat-jar.jar"
  )

                                      /** dependencies */
lazy val commonDependencies = Seq(
  akka_http,
  akka_streams,
  akka_http_core,
  akka_http_spray,
  kafka_clients,
  kafka_streams
)
lazy val observabilityDependencies = Seq(
  prometheus_simple_client,
  prometheus_common,
  prometheus_hot_spot
)
lazy val testDependencies = Seq(
  scala_test
)
