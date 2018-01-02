
name := """pop-the-corn-libraries"""

lazy val commonSettings = Seq(
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.8"
)
lazy val elastic4sVersion = "1.7.0"

lazy val scalaTest =   "org.scalatest" %% "scalatest" % "3.0.1" % Test
lazy val elastic4s = "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion

lazy val akkaActor = "com.typesafe.akka" % "akka-actor_2.11" % "2.5.4"

lazy val circeVersion = "0.8.0"
lazy val circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-optics"
).map(_ % circeVersion)

val libraries = Project(id = "libraries", base = file("."))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      elastic4s,
      akkaActor,
      scalaTest
    ) ++ circe
  )