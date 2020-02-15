package org.scalajs.tools.tsimporter

import java.io.{ PrintWriter, StringWriter }

import org.scalajs.tools.tsimporter.Trees.DeclTree
import org.scalajs.tools.tsimporter.parser.TSDefParser

import scala.scalajs.js
import scala.util.parsing.input.{ CharSequenceReader, Reader }

object Kicker {

  def translate(input: Input): ScalaOutput = {
    val reader = new CharSequenceReader(input.source)
    val outputPackage = input.outputPackage.filter(_.nonEmpty).getOrElse("foo")
    val config = Config(
      packageName = outputPackage,
      generateCompanionObject = input.generateFactory == "generate",
      forceAbstractFieldOnTrait = input.interfaceImplementation == "abstract"
    )
    parseDefinitions(reader, config)
  }

  private def parseDefinitions(reader: Reader[Char], config: Config): ScalaOutput = {
    val parser = new TSDefParser
    parser.parseDefinitions(reader) match {
      case parser.Success(tree, _) =>
        val writer = new StringWriter()
        process(tree, new PrintWriter(writer), config)
        new ScalaOutput(writer.getBuffer.toString, hasError = false)

      case parser.NoSuccess(msg, next) =>
        val errorMessage =
          s"""Parse error at ${next.pos.toString}
             |$msg
             |${next.pos.longString}""".stripMargin
        new ScalaOutput(errorMessage, hasError = true)
    }
  }

  private def process(definitions: List[DeclTree], output: PrintWriter, config: Config): Unit = {
    new Importer(output, config)(definitions)
  }
}

class Input(var source: String,
            var outputPackage: js.UndefOr[String] = js.undefined,
            var generateFactory: String = "generate",
            var interfaceImplementation: String = "abstract") extends js.Object

class ScalaOutput(var text: String, var hasError: Boolean) extends js.Object

class Sample(var url: String, var label: String) extends js.Object
