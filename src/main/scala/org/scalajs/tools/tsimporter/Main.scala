package org.scalajs.tools.tsimporter

import java.io.{PrintWriter, StringWriter}

import blog.codeninja.scalajs.vue._
import org.scalajs.tools.tsimporter.Trees._
import org.scalajs.tools.tsimporter.parser.TSDefParser

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.parsing.input._
import scala.util.{Failure, Success}


class Data( var sourceTypeScript: String,
             var isLoading: Boolean,
            var output: ScalaOutput) extends js.Object {
}

class ScalaOutput(var text: String, var hasError: Boolean) extends js.Object

object Main {
  var vue: Vue = _

  def main(args: Array[String]): Unit = {
    vue = new Vue(
      js.Dynamic.literal(
        el = "#app",

        data = new Data(
          sourceTypeScript = "",
          isLoading = false,
          output = new ScalaOutput(
            text = "",
            hasError = false
          )
        ),

        methods = js.Dynamic.literal(
          translate = translate: js.ThisFunction0[Data, _]
        )
      )
    )
  }

  def translate(data: Data): Unit = {
    data.isLoading = true

    import scala.scalajs.js.timers._

    setTimeout(100) { // note the absence of () =>
      val outputPackage = "foo"
      for {
        reader <- Future.successful(new CharSequenceReader(data.sourceTypeScript))
        output <- parseDefinitions(reader).transformWith {
          case Failure(exception) =>
            Future.successful(new ScalaOutput(exception.toString, hasError = true))
          case Success(tree) =>
            val writer = new StringWriter()
            process(tree, new PrintWriter(writer), outputPackage)
            Future.successful(new ScalaOutput(writer.getBuffer.toString, hasError = false))
        }
      } {
        data.isLoading = false
        data.output = output
      }
    }
  }


  private def process(definitions: List[DeclTree], output: PrintWriter,
                      outputPackage: String): Unit = {
    new Importer(output)(definitions, outputPackage)
  }

  private def parseDefinitions(reader: Reader[Char]): Future[List[DeclTree]] = {
    val parser = new TSDefParser
    parser.parseDefinitions(reader) match {
      case parser.Success(rawCode, _) =>
        Future.successful(rawCode)

      case parser.NoSuccess(msg, next) =>
        Future.failed(new Exception(
          s"""Parse error at ${next.pos.toString}
             |$msg
             |${next.pos.longString}""".stripMargin))
    }
  }
}
