import Common.Dependencies.{scalaTest, _}

name := """pop-the-corn"""

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtWeb)
  .settings(Common.settings: _*)
  .settings(
    libraryDependencies ++= Seq(
      jdbc,
      cache,
      ws,
      scalaTest,
      akkaActor,
      elastic4s
    )
  )

lazy val sparkV = "2.0.2"

lazy val spark = (project in file ("spark"))
.settings(Common.settings: _*)
.settings(
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % sparkV,
    "org.apache.spark" %% "spark-sql" % sparkV,
    "org.elasticsearch" % "elasticsearch-spark-20_2.11" % "5.5.1",
    csvReader
  )
)