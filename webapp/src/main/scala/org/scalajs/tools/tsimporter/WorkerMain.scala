package org.scalajs.tools.tsimporter


import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, JSGlobalScope}

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
    val s = msg.data.asInstanceOf[Input]
    WorkerGlobal.postMessage(s"[Worker] Received message")

    val so = translate(s)
    WorkerGlobal.postMessage(so)
    WorkerGlobal.postMessage(s"[Worker] Sent result")
  }

  def translate(input: Input): ScalaOutput = {
    Kicker.translate(input)
  }
}
