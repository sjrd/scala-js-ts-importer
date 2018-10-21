package org.scalajs.tools.tsimporter

import java.io.File
import org.scalatest.FunSpec
import scala.io.Source

class ImporterSpec extends FunSpec {
  def contentOf(file: File) =
    Source.fromFile(file).getLines.mkString("\n")

  describe("Main.main") {
    val inputDirectory = new File("samples")

    val outputDir = new File("target/tsimporter-test")
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
        assert(Right(()) == Main.importTsFile(config))
        assert(output.exists())
        assert(contentOf(output) == contentOf(expected))
      }
    }
  }

  describe("Config") {
    describe("interface variables") {
      val testCase = "interface-variables"
      val inputDirectory = new File(s"samples/config/$testCase")
      val input = new File(inputDirectory, s"$testCase.d.ts")
      val outputDir = new File(s"target/tsimporter-test-config/$testCase")
      Option(outputDir.listFiles()).foreach(_.foreach(_.delete()))
      outputDir.mkdirs()

      it("can be implemented (js.native) in default configuration") {
        val expected = new File(inputDirectory, "implemented.scala")
        val output = new File(outputDir,  "implemented.scala")
        val config = Config(
            inputFileName = input.getAbsolutePath,
            outputFileName = output.getAbsolutePath,
            packageName = input.getName.takeWhile(_ != '.')
        )
        assert(Right(()) == Main.importTsFile(config))
        assert(output.exists())
        assert(contentOf(output) == contentOf(expected))
      }

      it("can be abstract forcibly") {
        val expected = new File(inputDirectory, "abstract.scala")
        val output = new File(outputDir,  "abstract.scala")
        val config = Config(
            inputFileName = input.getAbsolutePath,
            outputFileName = output.getAbsolutePath,
            packageName = input.getName.takeWhile(_ != '.'),
            forceAbstractFieldOnTrait = true
        )
        assert(Right(()) == Main.importTsFile(config))
        assert(output.exists())
        assert(contentOf(output) == contentOf(expected))
      }
    }
  }
}
