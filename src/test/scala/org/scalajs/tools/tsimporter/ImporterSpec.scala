package org.scalajs.tools.tsimporter

import org.scalatest.FunSpec

class ImporterSpec extends FunSpec {

  val useDefaultConfig: Config => Config = identity

  describe("Main.main") {
    val tester = Tester(inputDirectoryName = "samples")

    for (input <- tester.inputDir.listFiles() if input.getName.endsWith(".ts")) {
      it(s"should import ${input.getName}") {
        tester.compareContent(input.getName, s"${input.getName}.scala")(useDefaultConfig)
      }
    }
  }

  describe("Config") {
    describe("Fields of TypeScript Interface") {
      val tester = Tester(inputDirectoryName = "samples/config/interface-variables")
      val inputName = "source.d.ts"

      it("should be implemented (js.native) in default configuration") {
        tester.compareContent(inputName, "implemented.scala")(useDefaultConfig)
      }

      it("can be abstract forcibly") {
        tester.compareContent(inputName, "abstract.scala")(_.copy(forceAbstractFieldOnTrait = true))
      }
    }
  }
}
