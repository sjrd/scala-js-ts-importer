package org.scalajs.tools.tsimporter

import io.scalajs.nodejs.fs.Fs
import org.scalatest.funspec.AnyFunSpec

class ImporterConfigSpec extends AnyFunSpec {

  private def contentOf(file: String) = Fs.readFileSync(file, "utf-8")

  describe("generateFactory") {
    it("should generate factory") {
      val generateFactory = true
      val sourceTypeScript = s"samples/config/generateFactory/${generateFactory}.d.ts"
      val expectedContent = contentOf(sourceTypeScript + ".scala")
      val outputContent = WorkerMain.translate(new Input(
        source = contentOf(sourceTypeScript),
        outputPackage = "factory",
        generateFactory = generateFactory
      ))
      assert(!outputContent.hasError, s"hasError: ${ outputContent.text }")
      assert(outputContent.text === expectedContent)
    }

    it("should NOT generate factory") {
      val generateFactory = false
      val sourceTypeScript = s"samples/config/generateFactory/${generateFactory}.d.ts"
      val expectedContent = contentOf(sourceTypeScript + ".scala")
      val outputContent = WorkerMain.translate(new Input(
        source = contentOf(sourceTypeScript),
        outputPackage = "factory",
        generateFactory = generateFactory
      ))
      assert(!outputContent.hasError, s"hasError: ${ outputContent.text }")
      assert(outputContent.text === expectedContent)
    }
  }
}
