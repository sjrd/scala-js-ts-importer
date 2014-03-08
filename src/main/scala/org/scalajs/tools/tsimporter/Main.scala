/* TypeScript importer for Scala.js
 * Copyright 2013-2014 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package org.scalajs.tools.tsimporter

import java.io.{ Console => _, Reader => _, _ }

import scala.collection.immutable.PagedSeq

import Trees._

import scala.util.parsing.input._
import parser.TSDefParser

/** Entry point for the TypeScript importer of Scala.js */
object Main {
  def main(args: Array[String]) {
    if (args.length < 2) {
      Console.err.println("""
        |Usage: scalajs-ts-importer <input.d.ts> <output.scala> [<package>]
        |  <input.d.ts>     TypeScript type definition file to read
        |  <output.scala>   Output Scala.js file
        |  <package>        Package name for the output (defaults to "importedjs")
      """.stripMargin.trim)
      System.exit(1)
    }

    val inputFileName = args(0)
    val outputFileName = args(1)
    val outputPackage = if (args.length > 2) args(2) else "importedjs"

    val definitions = parseDefinitions(readerForFile(inputFileName))

    val output = new PrintWriter(new BufferedWriter(
        new FileWriter(outputFileName)))
    try {
      process(definitions, output, outputPackage)
    } finally {
      output.close()
    }
  }

  private def process(definitions: List[DeclTree], output: PrintWriter,
      outputPackage: String) {
    new Importer(output)(definitions, outputPackage)
  }

  private def parseDefinitions(reader: Reader[Char]): List[DeclTree] = {
    val parser = new TSDefParser
    parser.parseDefinitions(reader) match {
      case parser.Success(rawCode, _) =>
        rawCode

      case parser.NoSuccess(msg, next) =>
        Console.err.println(
            "Parse error at %s\n".format(next.pos.toString) +
            msg + "\n" +
            next.pos.longString)
        sys.exit(2)
    }
  }

  /** Builds a [[scala.util.parsing.input.PagedSeqReader]] for a file
   *
   *  @param fileName name of the file to be read
   */
  private def readerForFile(fileName: String) = {
    new PagedSeqReader(PagedSeq.fromReader(
        new BufferedReader(new FileReader(fileName))))
  }
}
