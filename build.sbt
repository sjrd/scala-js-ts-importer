scalaVersion := "2.10.2"

name := "TypeScript importer for Scala.js"

version := "0.1-SNAPSHOT"

mainClass := Some("scala.tools.scalajs.tsimporter.Main")

organization := "ch.epfl.lamp"

scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-encoding", "utf8"
)
