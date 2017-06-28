scalaVersion := "2.11.7"

name := "TypeScript importer for Scala.js"

version := "0.1-SNAPSHOT"

mainClass := Some("org.scalajs.tools.tsimporter.Main")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2",
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

organization := "org.scalajs.tools"

scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-encoding", "utf8"
)

fork in Test := true
