import sbt._
import sbt.Keys.{scalaVersion, _}

object Common {

  lazy val settings: Seq[Setting[_]] = Seq(
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.7"
  )

  object Dependencies {

    lazy val elastic4sVersion = "1.7.0"

    lazy val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
    lazy val elastic4s = "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion
    lazy val csvReader = "com.github.tototoshi" %% "scala-csv" % "1.3.4"
    lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.1.1"

  }

}