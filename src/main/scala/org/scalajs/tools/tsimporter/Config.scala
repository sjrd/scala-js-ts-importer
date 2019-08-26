package org.scalajs.tools.tsimporter

case class Config(
    inputFileName: String = "",
    outputFileName: String = "",
    packageName: String = "importedjs",
    generateCompanionObject: Boolean = false
)
