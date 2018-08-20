// Project setup
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
lazy val allDependencies = new {

  val akkaHttpV     = "10.1.3"
  val akkaStreamsV  = "2.5.14"
  val scalaTestV    = "3.0.4"
  val slickV        = "3.2.3"

  val akkaHttp              = "com.typesafe.akka"   %% "akka-http"            % akkaHttpV
  val akkaStreams           = "com.typesafe.akka"   %% "akka-stream"          % akkaStreamsV
  val akkaHttpCore          = "com.typesafe.akka"   %% "akka-http-core"       % akkaHttpV
  val akkaHttpSpray         = "com.typesafe.akka"   %% "akka-http-spray-json" % akkaHttpV

  val kafkaV        = "1.0.0"

  val kClients              = "org.apache.kafka"    % "kafka-clients"         % kafkaV



  // observability (Logs, Metrics, Tracing)
  val prometheusMetricsV    = "0.5.0"

  val pSimpleClient         = "io.prometheus"     % "simpleclient"            % prometheusMetricsV
  val pCommon               = "io.prometheus"     % "simpleclient_common"     % prometheusMetricsV
  val pHotSpot              = "io.prometheus"     % "simpleclient_hotspot"    % prometheusMetricsV

  // test dependencies
  val scalaTest             = "org.scalatest"       %% "scalatest"            % scalaTestV % Test

}

lazy val commonDependencies = Seq(
  allDependencies.akkaHttp,
  allDependencies.akkaStreams,
  allDependencies.akkaHttpCore,
  allDependencies.akkaHttpSpray,
  allDependencies.kClients
)

lazy val observabilityDependencies = Seq(
  allDependencies.pSimpleClient,
  allDependencies.pCommon,
  allDependencies.pHotSpot
)

lazy val testDependencies = Seq(
  allDependencies.scalaTest
)
