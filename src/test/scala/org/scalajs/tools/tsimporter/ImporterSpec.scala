package org.scalajs.tools.tsimporter

import java.io.{ File, FilenameFilter }
import org.scalatest.FunSpec
import scala.io.Source

class ImporterSpec extends FunSpec {
  describe("Main.main") {
    val inputDirectory = new File("samples")

    val outputDir = new File("target/tsimporter-test")
    Option(outputDir.listFiles()).foreach(_.foreach(_.delete()))
    outputDir.mkdirs()

    def contentOf(file: File) =
      Source.fromFile(file).getLines.mkString("\n")

    for (input <- inputDirectory.listFiles() if input.getName.endsWith(".ts")) {
      it(s"should import ${input.getName}") {
        val expected = new File(inputDirectory, input.getName + ".scala")
        val output = new File(outputDir, input.getName + ".scala")

        assert(Right(()) == Main.importTsFile(input.getAbsolutePath, output.getAbsolutePath, input.getName.takeWhile(_ != '.')))

        assert(output.exists())
        assert(contentOf(output) == contentOf(expected))
      }
    }
  }
}
