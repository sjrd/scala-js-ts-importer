package org.scalajs.tools.tsimporter

import io.scalajs.nodejs.fs.Fs
import org.scalatest.funspec.AnyFunSpec

class ImporterConfigSpec extends AnyFunSpec {

  private def contentOf(file: String) = Fs.readFileSync(file, "utf-8")

  describe("generateFactory") {
    it("should generate factory") {
      val generateFactory = "generate"
      val sourceTypeScript = s"samples/config/generateFactory/${generateFactory}.d.ts"
      val expectedContent = contentOf(sourceTypeScript + ".scala")
      val outputContent = WorkerMain.translate(new Input(
        source = contentOf(sourceTypeScript),
        outputPackage = "factory",
        generateFactory = generateFactory,
        interfaceImplementation = "implemented"
      ))
      assert(!outputContent.hasError, s"hasError: ${ outputContent.text }")
      assert(outputContent.text === expectedContent)
    }

    it("should NOT generate factory") {
      val generateFactory = "donot"
      val sourceTypeScript = s"samples/config/generateFactory/${generateFactory}.d.ts"
      val expectedContent = contentOf(sourceTypeScript + ".scala")
      val outputContent = WorkerMain.translate(new Input(
        source = contentOf(sourceTypeScript),
        outputPackage = "factory",
        generateFactory = generateFactory,
        interfaceImplementation = "implemented"
      ))
      assert(!outputContent.hasError, s"hasError: ${ outputContent.text }")
      assert(outputContent.text === expectedContent)
    }
  }
  
  describe("interface-variables") {
    it("js-native implemented") {
      val sourceTypeScript = "samples/config/interface-variables/source.d.ts"
      val expectedContent = contentOf("samples/config/interface-variables/implemented.scala")
      val outputContent = WorkerMain.translate(new Input(
        source = contentOf(sourceTypeScript),
        outputPackage = "source",
        generateFactory = "donot",
        interfaceImplementation = "implemented"
      ))
      assert(!outputContent.hasError, s"hasError: ${ outputContent.text }")
      assert(outputContent.text === expectedContent)
    }
    
    it("abstract") {
      val sourceTypeScript = "samples/config/interface-variables/source.d.ts"
      val expectedContent = contentOf("samples/config/interface-variables/abstract.scala")
      val outputContent = WorkerMain.translate(new Input(
        source = contentOf(sourceTypeScript),
        outputPackage = "source",
        generateFactory = "donot",
        interfaceImplementation = "abstract"
      ))
      assert(!outputContent.hasError, s"hasError: ${ outputContent.text }")
      assert(outputContent.text === expectedContent)
    }
  }
}
