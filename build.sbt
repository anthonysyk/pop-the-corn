name := """pop-the-corn"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.7"

val elastic4sVersion = "1.7.0"


libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion
)