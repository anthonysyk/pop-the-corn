import Common.Dependencies.{scalaTest, _}

name := """pop-the-corn"""

lazy val api = (project in file("api"))
  .settings(Common.settings: _*)
  .settings(
    libraryDependencies ++= Seq(
      elastic4s,
      akkaHttp,
      akkaActor,
      akkaStream,
      "org.slf4j" % "slf4j-api" % "1.7.25"
    ) ++ circe
  )
  .dependsOn(libraries)

lazy val indexer = (project in file("indexer"))
  .settings(Common.settings: _*)
  .settings(
    libraryDependencies ++= Seq(
      elastic4s,
      akkaActor,
      scalaTest,
      "org.elasticsearch" % "elasticsearch-spark-20_2.11" % "5.5.1",
      "org.apache.hadoop" % "hadoop-common" % "2.7.2",
      "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.7.2",
      cats
    ) ++ circe ++ sparkDependencies
  )
  .dependsOn(libraries)

lazy val mlapi = (project in file("mlapi"))
  .settings(Common.settings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.elasticsearch" % "elasticsearch-spark-20_2.11" % "5.5.1",
      "org.apache.hadoop" % "hadoop-common" % "2.7.2",
      "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.7.2",
      "com.quantifind" %% "wisp" % "0.0.4",
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",
      "org.sangria-graphql" %% "sangria" % "1.3.0"
      csvReader
    ) ++ sparkDependencies
  )
  .dependsOn(libraries)

lazy val libraries = (project in file("libraries"))
  .settings(Common.settings: _*)
  .settings(
    libraryDependencies ++= Seq(
      elastic4s,
      akkaActor,
      scalaTest,
      cats
    ) ++ circe
  )
