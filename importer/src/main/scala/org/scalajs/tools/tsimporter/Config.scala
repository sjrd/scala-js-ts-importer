package org.scalajs.tools.tsimporter

case class Config(
  inputFileName: String = "",
  outputFileName: String = "",
  packageName: String = "importedjs",
  factoryConfig: FactoryConfig,
  forceAbstractFieldOnTrait: Boolean = false
)

sealed trait FactoryConfig {
  def generateFactory: Boolean
  def useTrailingComma: Boolean
}
object FactoryConfig {
  case object Generate extends FactoryConfig {
    val generateFactory:Boolean = true
    val useTrailingComma: Boolean = true
  }
  case object GenerateNoTrailingComma extends FactoryConfig {
    val generateFactory:Boolean = true
    val useTrailingComma: Boolean = false
  }
  case object DoNotGenerate extends FactoryConfig {
    val generateFactory:Boolean = false
    val useTrailingComma: Boolean = false
  }
}
