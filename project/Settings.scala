import sbt._
import sbt.Keys._
import sbtassembly.AssemblyKeys.assembly

// https://www.scala-sbt.org/release/docs/Basic-Def-Examples.html
case class Settings(scalaV:String) {
  lazy val default = Seq(
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
}
