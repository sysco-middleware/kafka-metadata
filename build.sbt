// Project setup
import scalariform.formatter.preferences._
import Dependencies._

//val rootPackage = "no.sysco.middleware"
//val subRootPackage = s"$rootPackage.ktm"
val projectV = "0.0.1-SNAPSHOT"
val scalaV = "2.12.6"

// https://www.scala-sbt.org/release/docs/Basic-Def-Examples.html
lazy val settings = Seq(

//  organization := s"$subRootPackage",
//  version := projectV,
  scalaVersion := scalaV,

  test in assembly := {},

  // set the main Scala source directory to be <base>/src
  scalaSource in Compile := baseDirectory.value / "src/main/scala",

  // set the Scala test source directory to be <base>/test
  scalaSource in Test := baseDirectory.value / "src/test/scala",

  // append several options to the list of options passed to the Java compiler
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),

  // set the initial commands when entering 'console' or 'consoleQuick', but not 'consoleProject'
  initialCommands in console := "import no.sysco.middleware.ktm._",

  // only use a single thread for building
  parallelExecution := false,

  //Run tests Sequentially
  parallelExecution in Test := false

)

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
//  .aggregate(
//    utils,
//    tmetadata
//  )

//lazy val tmetadata = project
//  .settings(
//    name := "tmetadata",
//    settings,
//    libraryDependencies ++= commonDependencies ++ observabilityDependencies ++ testDependencies,
//
//    mainClass in assembly := Some(s"$subRootPackage.tmetadata.Main"),
//    assemblyJarName in assembly := "tmetadata-fat-jar.jar"
//  )
//  .dependsOn(utils)

//lazy val utils = project
//  .settings(
//    name := "utils",
//    settings,
//    libraryDependencies ++= Seq(
//      kafka_clients,
//      kafka_streams
//    )
//  )

                                      /** dependencies */
lazy val commonDependencies = Seq(
  akka_http,
  akka_streams,
  akka_http_core,
  akka_http_spray,
  kafka_clients,
  kafka_streams,
  akka_slf4j
)
lazy val observabilityDependencies = Seq(
  prometheus_simple_client,
  prometheus_common,
  prometheus_hot_spot
)
lazy val testDependencies = Seq(
  scala_test
)

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)
