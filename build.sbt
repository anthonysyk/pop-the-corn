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
      csvReader,
      akkaActor,
      elastic4s
    )
  )