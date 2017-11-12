package org.scalajs.tools.tsimporter


import java.io.{PrintWriter, StringWriter}

import org.scalajs.dom
import org.scalajs.tools.tsimporter.Trees.DeclTree
import org.scalajs.tools.tsimporter.parser.TSDefParser

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, JSGlobalScope}
import scala.util.parsing.input.{CharSequenceReader, Reader}

@js.native
@JSGlobalScope
object WorkerGlobal extends js.Object {
  def addEventListener(`type`: String, f: js.Function): Unit = js.native

  def postMessage(data: js.Any): Unit = js.native
}

@JSExportTopLevel("WorkerMain")
object WorkerMain {
  @JSExport
  def main(): Unit = {
    WorkerGlobal.addEventListener("message", onMessage _)
    WorkerGlobal.postMessage(s"[Worker] Started")
  }

  def onMessage(msg: dom.MessageEvent) = {
    val s = msg.data.asInstanceOf[String]
    WorkerGlobal.postMessage(s"[Worker] Received message")

    val so = translate(s)
    WorkerGlobal.postMessage(so)
    WorkerGlobal.postMessage(s"[Worker] Sent result")
  }

  private def translate(sourceTypeScript: String): ScalaOutput = {
    val reader = new CharSequenceReader(sourceTypeScript)
    parseDefinitions(reader)
  }

  private def parseDefinitions(reader: Reader[Char]): ScalaOutput = {
    val outputPackage = "foo"
    val parser = new TSDefParser
    parser.parseDefinitions(reader) match {
      case parser.Success(tree, _) =>
        val writer = new StringWriter()
        process(tree, new PrintWriter(writer), outputPackage)
        new ScalaOutput(writer.getBuffer.toString, hasError = false)

      case parser.NoSuccess(msg, next) =>
        val errorMessage =
          s"""Parse error at ${next.pos.toString}
             |$msg
             |${next.pos.longString}""".stripMargin
        new ScalaOutput(errorMessage, hasError = true)
    }
  }

  private def process(definitions: List[DeclTree], output: PrintWriter, outputPackage: String): Unit = {
    new Importer(output)(definitions, outputPackage)
  }

}