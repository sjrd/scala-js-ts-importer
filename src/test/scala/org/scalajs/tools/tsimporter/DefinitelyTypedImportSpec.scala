package org.scalajs.tools.tsimporter

import java.io.File

import org.scalatest.FunSpec

class DefinitelyTypedImportSpec extends FunSpec {

  val typesDir = new File("DefinitelyTyped/types")

  describe("DefinitelyTyped") {
    it("should be present into the root of the directory") {
      assert(typesDir.exists(), "DefinitelyTyped not present. Please clone https://github.com/DefinitelyTyped/DefinitelyTyped")
    }
  }

  describe("scala-js-ts-importer") {

    case class DTProject(name: String, file: File)

    val dtProjects = Option(typesDir.listFiles())
      .getOrElse(Array.empty)
      .filter(_.isDirectory)
      .map(dir => DTProject(dir.getName, dir.toPath.resolve("index.d.ts").toFile))
      .filter(_.file.isFile)

    val tempOutputFile = File.createTempFile("scala-js-ts-importer-test-", ".scala")
    tempOutputFile.deleteOnExit()
    val tempOutputFileName = tempOutputFile.getAbsolutePath

    for (DTProject(name, file) <- dtProjects) it(s"should import '$name'") {
      val fileName = file.getCanonicalPath
      Main.importTsFile(fileName, tempOutputFileName, "test") match {
        case Left(errorMessage) =>
          fail(s"failed importing $fileName: $errorMessage")
        case Right(()) =>
      }
    }
  }
}
