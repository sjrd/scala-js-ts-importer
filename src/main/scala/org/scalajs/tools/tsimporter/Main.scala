/* TypeScript importer for Scala.js
 * Copyright 2013-2014 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package org.scalajs.tools.tsimporter

import blog.codeninja.scalajs.vue._
import org.scalajs.dom.console
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.webworkers.Worker


import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


class Data(var sourceTypeScript: String,
           var isLoading: Boolean,
           var output: ScalaOutput) extends js.Object {
}

class ScalaOutput(var text: String, var hasError: Boolean) extends js.Object

@JSExportTopLevel("Main")
object Main {
  var vue: Vue = _

  @JSExport
  def main(): Unit = {
    main(js.Array[String]())
  }

  @JSExport
  def main(args: js.Array[String]): Unit = {
    main(args.toArray)
  }

  def main(args: Array[String]): Unit = {
    val worker = new Worker("worker.js")

    val data = new Data(
      sourceTypeScript = "",
      isLoading = false,
      output = new ScalaOutput(
        text = "",
        hasError = false
      )
    )

    worker.onmessage = (e: js.Any) => {
      val event = e.asInstanceOf[MessageEvent].data
      event match {
        case str: String => console.info(str)
        case _ =>
          // Uses type-cast instead of pickler, to minimize dependency.
          val obj = event.asInstanceOf[js.Dynamic]
          data.output = new ScalaOutput(obj.text.asInstanceOf[String], obj.hasError.asInstanceOf[Boolean])
          data.isLoading = false
      }
    }

    vue = new Vue(js.Dynamic.literal(
      el = "#app",
      data = data,
      methods = js.Dynamic.literal(
        translate = js.ThisFunction.fromFunction1 { data: Data =>
          console.info("[Main] send to worker")
          data.isLoading = true
          worker.postMessage(data.sourceTypeScript)
        }
      )
    ))
  }
}
