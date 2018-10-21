package org.scalajs.tools.tsimporter

import java.io.File
import org.scalatest.FunSpec
import scala.io.Source

case class Tester private(inputDirectoryName: String)  extends FunSpec {
  val inputDir: File = new File(inputDirectoryName)
  val outputDir: File = {
    val outputDir = new File(s"target/tsimporter-test/$inputDirectoryName")
    Option(outputDir.listFiles()).foreach(_.foreach(_.delete()))
    outputDir.mkdirs()
    outputDir
  }

  private def contentOf(file: File): String = {
    Source.fromFile(file).getLines.mkString("\n")
  }

  def compareContent(inputName: String, expectedName: String)(configPatch: Config => Config): Unit ={
    val input = new File(inputDir, inputName)
    val expected = new File(inputDir, expectedName)
    val output = new File(outputDir, expectedName)

    val importerConfig = configPatch(Config(
      inputFileName = input.getAbsolutePath,
      outputFileName = output.getAbsolutePath,
      packageName = input.getName.takeWhile(_ != '.')
    ))

    assert(Right(()) === Main.importTsFile(importerConfig))
    assert(output.exists())
    assert(contentOf(output) === contentOf(expected))
  }
}
