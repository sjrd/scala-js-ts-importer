package org.scalajs.tools.tsimporter

case class Config(
    inputFileName: String = "",
    outputFileName: String = "",
    packageName: String = "importedjs",
    forceAbstractFieldOnTrait: Boolean = false
)

object Config {
  final val parser = new scopt.OptionParser[Config]("scalajs-ts-importer") {
    arg[String]("<input.d.ts>").required()
      .text("TypeScript type definition file to be read")
      .action((i, config) => config.copy(inputFileName = i))

    arg[String]("<output.scala>").required()
      .text("Output Scala.js file")
      .action((o, config) => config.copy(outputFileName = o))

    arg[String]("<package>").optional()
      .text("Package name for the output (defaults to \"importedjs\")")
      .action((pn, config) => config.copy(packageName = pn))

    opt[Unit]("forceAbstractFieldOnTrait").optional()
      .text("Leave fields on TypeScript interfaces abstract (defaults to implemented \"var field: String = js.native\")")
      .action((_, config) => config.copy(forceAbstractFieldOnTrait = true))

    help("help").abbr("h")
      .text("prints help")
  }
}
