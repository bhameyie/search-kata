lazy val akkaHttpVersion = "10.0.7"

lazy val akkaVersion = "2.4.18"

lazy val circeVersion = "0.8.0"

lazy val shared = (project in file("shared")).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "org.mongodb.scala" %% "mongo-scala-driver" % "2.0.0",
      "org.typelevel" %% "cats" % "0.9.0"
    )
  )

lazy val searchApi = (project in file("search-api")).
  settings(
    name := "search-api",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "de.heikoseeberger" %% "akka-http-circe" % "1.16.1",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-jawn" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.apache.commons" % "commons-lang3" % "3.5",
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "org.scalatest" %% "scalatest" % "3.0.1" % Test
    )
  ).dependsOn(shared)

lazy val importer = project
  .settings(
    name := "data-importer",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
    )
  ).dependsOn(shared)

lazy val root = (project in file(".")).
  settings(inThisBuild(List(
    organization := "com.bhameyie",
    scalaVersion := "2.12.2"
  )), name := "suggester")
  .aggregate(searchApi, importer, shared)