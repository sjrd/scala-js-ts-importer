import sbt.Keys.{ artifactPath, resolvers }

inThisBuild(Def.settings(
  organization := "org.scalajs.tools",
  version := "0.1-SNAPSHOT",
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-encoding", "utf8"
  )
))

lazy val `scala-js-ts-importer` = project.in(file("."))
  .aggregate(importer, webapp, samples)

lazy val webapp = project.in(file("webapp"))
  .settings(
    resolvers += "jitpack" at "https://jitpack.io",
    description := "TypeScript importer for Scala.js",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0"
    ),
    scalaJSUseMainModuleInitializer := false,
    scalaJSLinkerConfig ~= { 
      _.withSourceMap(false)
    },
    Seq(fastOptJS, fullOptJS) map { packageJSKey =>
      artifactPath in (Compile, packageJSKey) := (resourceDirectory in Compile).value / (moduleName.value + "-opt.js")
    }
  )
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(importer)

lazy val importer = project.in(file("importer"))
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.2",
      "net.exoego" %%% "scala-js-nodejs-v12" % "0.11.0" % Test,
      "org.scalatest" %%% "scalatest" % "3.1.2" % Test
    ),
    scalaJSLinkerConfig ~= {
      _.withSourceMap(false)
        .withModuleKind(ModuleKind.CommonJSModule)
    },
  )
  .enablePlugins(ScalaJSPlugin)

lazy val samples = project
  .enablePlugins(ScalaJSPlugin)

