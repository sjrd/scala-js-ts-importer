package org.scalajs.tools.tsimporter

import io.scalajs.nodejs.fs.Fs
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec

class ImporterSpec extends AnyFunSpec with BeforeAndAfterAll {

  private val outputDir = "target/tsimporter-test"

  override def beforeAll() = {
    Fs.mkdirSync(outputDir)
  }

  override def afterAll() = {
    if (Fs.existsSync(outputDir)) {
      Fs.rmdirSync(outputDir)
    }
  }

  private def contentOf(file: String) = Fs.readFileSync(file, "utf-8")

  describe("Main.main") {
    val inputDir = "samples"
    for (input <- Fs.readdirSync(inputDir) if input.endsWith(".ts")) {
      it(s"should import ${ input }") {
        val sourceTypeScript = inputDir + "/" + input
        val expectedContent = contentOf(sourceTypeScript + ".scala")
        val outputContent = WorkerMain.translate(new Input(
          source = contentOf(sourceTypeScript),
          outputPackage = input.dropRight(".d.ts".length),
          generateFactory = false
        ))
        assert(outputContent.text === expectedContent)
      }
    }
  }
}
