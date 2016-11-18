scalaVersion := "2.11.8"

name := "TypeScript importer for Scala.js"

version := "0.1-SNAPSHOT"

mainClass := Some("org.scalajs.tools.tsimporter.Main")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scalameta" %% "scalameta" % "1.1.0",
  "com.geirsson" %% "scalafmt-cli" % "0.4.10"
)

organization := "org.scalajs.tools"

scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-encoding", "utf8"
)
