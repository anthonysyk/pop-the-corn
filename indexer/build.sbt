name := """pop-the-corn-indexer"""

lazy val commonSettings = Seq(
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val elastic4sVersion = "1.7.0"

lazy val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
lazy val elastic4s = "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion
lazy val akkaActor = "com.typesafe.akka" % "akka-actor_2.11" % "2.5.4"

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

lazy val libraries = RootProject(file("../libraries"))

val main = Project(id = "indexer", base = file("."))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      elastic4s,
      akkaActor,
      scalaTest,
      "org.elasticsearch" % "elasticsearch-spark-20_2.11" % "5.5.1",
      "org.apache.hadoop" % "hadoop-common" % "2.7.2",
      "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.7.2"
    ) ++ circe ++ sparkDependencies
  )
  .dependsOn(libraries)

//packSettings
//
//packResourceDir ++= Map(baseDirectory.value / "mlapi/src/main/resources" -> "resources")
//
//packMain := Map("start-exploit-tfidf" -> "nlp.ExploitTFIDF")
