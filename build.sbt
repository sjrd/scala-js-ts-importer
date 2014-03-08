scalaVersion := "2.10.2"

name := "TypeScript importer for Scala.js"

version := "0.1-SNAPSHOT"

mainClass := Some("org.scalajs.tools.tsimporter.Main")

organization := "org.scalajs.tools"

scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-encoding", "utf8"
)
