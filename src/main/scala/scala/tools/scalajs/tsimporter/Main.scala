/* TypeScript importer for Scala.js
 * Copyright 2013 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package scala.tools.scalajs.tsimporter

import java.io.{ Console => _, Reader => _, _ }

import scala.collection.immutable.PagedSeq

import Trees._

import scala.util.parsing.input._
import parser.TSDefParser

/** Entry point for the TypeScript importer of Scala.js */
object Main {
  def main(args: Array[String]) {
    args match {
      case Array(inputFileName, outputFileName, _*) =>
        val outputPackage = if (args.length > 2) args(2) else "importedjs"

        val definitions = parseDefinitions(readerForFile(inputFileName))

        val output = new PrintWriter(new BufferedWriter(
          new FileWriter(outputFileName)))
        try {
          process(definitions, output, outputPackage)
        } finally {
          output.close()
        }

      case _ => println(""" |Typescript Importer <inputFile> <outputFile> <outputPackage>
                            |   You need to specifiy 2 arguments for the importer
                            |   The "outputPackage" has the default value of "importedjs"
                            |""".stripMargin)
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

  /**
   * Builds a [[scala.util.parsing.input.PagedSeqReader]] for a file
   *
   *  @param fileName name of the file to be read
   */
  private def readerForFile(fileName: String) = {
    new PagedSeqReader(PagedSeq.fromReader(
      new BufferedReader(new FileReader(fileName))))
  }
}
