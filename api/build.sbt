name := """pop-the-corn-api"""

lazy val commonSettings = Seq(
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val elastic4sVersion = "1.7.0"

lazy val elastic4s = "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion
lazy val akkaActor = "com.typesafe.akka" % "akka-actor_2.11" % "2.5.4"
lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.10"
lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.5.4"

lazy val circeVersion = "0.8.0"
lazy val circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-optics"
).map(_ % circeVersion)

lazy val sparkVersion = "2.0.2"
lazy val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core",
  "org.apache.spark" %% "spark-sql",
  "org.apache.spark" %% "spark-mllib"
).map(_ % sparkVersion)

lazy val scalaTest = "org.scalatest" % "scalatest_2.11" % "3.0.1" % "test"


lazy val libraries = RootProject(file("../libraries"))

val main = Project(id = "api", base = file("."))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      elastic4s,
      akkaHttp,
      akkaActor,
      akkaStream,
      scalaTest
    ) ++ circe
  )
  .dependsOn(libraries)

packMain := Map(
  "run-webservice" -> "WebServer"
)

packResourceDir ++= Map(
  baseDirectory.value / "src/main/bin" -> "bin",
  baseDirectory.value / "src/main/resources" -> "conf",
  baseDirectory.value / "../front/assets" -> "assets",
  baseDirectory.value / "src/main/logs" -> "logs",
  baseDirectory.value / "Dockerfile" ->  "Dockerfile"
)