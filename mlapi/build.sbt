name := """pop-the-corn-mlapi"""

lazy val commonSettings = Seq(
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val elastic4sVersion = "1.7.0"

lazy val csvReader = "com.github.tototoshi" %% "scala-csv" % "1.3.4"

val akkaVersion = "2.5.4"
val akkaHttpVersion = "10.0.10"
libraryDependencies += "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor"  % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion

lazy val sparkVersion = "2.0.2"
lazy val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core",
  "org.apache.spark" %% "spark-sql",
  "org.apache.spark" %% "spark-mllib"
).map(_ % sparkVersion)

lazy val libraries = RootProject(file("../libraries"))

val mlapi = Project(id = "mlapi", base = file("."))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.elasticsearch" % "elasticsearch-spark-20_2.11" % "5.5.1",
      "org.apache.hadoop" % "hadoop-common" % "2.7.2",
      "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.7.2",
      "com.quantifind" %% "wisp" % "0.0.4",
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",
      "org.sangria-graphql" %% "sangria" % "1.3.0",
      "org.sangria-graphql" %% "sangria-spray-json" % "1.0.0",
      csvReader
    ) ++ sparkDependencies
  )
  .dependsOn(libraries)

packSettings

packResourceDir ++= Map(baseDirectory.value / "src/main/resources" -> "resources")

packMain := Map("start-exploit-tfidf" -> "nlp.ExploitTFIDF")