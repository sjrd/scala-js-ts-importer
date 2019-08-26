package org.scalajs.tools.tsimporter

import java.io.{ File, FilenameFilter }
import org.scalatest.FunSpec
import scala.io.Source

class ImporterSpec extends FunSpec {
  val outputDir = new File("target/tsimporter-test")

  def contentOf(file: File) =
    Source.fromFile(file).getLines.mkString("\n")

  describe("Main.main") {
    val inputDirectory = new File("samples")
    Option(outputDir.listFiles()).foreach(_.foreach(_.delete()))
    outputDir.mkdirs()

    for (input <- inputDirectory.listFiles() if input.getName.endsWith(".ts")) {
      it(s"should import ${input.getName}") {
        val expected = new File(inputDirectory, input.getName + ".scala")
        val output = new File(outputDir, input.getName + ".scala")

        val config = Config(
          inputFileName = input.getAbsolutePath,
          outputFileName = output.getAbsolutePath,
          packageName = input.getName.takeWhile(_ != '.')
        )
        
        assert(Right(()) == Main.importTsFile(input.getAbsolutePath, config))
        assert(output.exists())
        assert(contentOf(output) == contentOf(expected))
      }
    }
  }
  
  describe("Config") {
    it("should generate factory") {
      val input  = new File("samples/config", "factory.d.ts")
      val expected = new File("samples/config", "factory.d.ts.scala")
      val output = new File(outputDir, "samples-factory.d.ts.scala")

      val config = Config(
        inputFileName = input.getAbsolutePath,
        outputFileName = output.getAbsolutePath,
        packageName = input.getName.takeWhile(_ != '.'),
        generateCompanionObject = true
      )

      assert(Right(()) == Main.importTsFile(input.getAbsolutePath, config))
      assert(output.exists())
      assert(contentOf(output) == contentOf(expected))
    }
  }
  
}
