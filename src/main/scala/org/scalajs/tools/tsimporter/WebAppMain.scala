package org.scalajs.tools.tsimporter

import blog.codeninja.scalajs.vue._
import org.scalajs.dom.webworkers.Worker
import org.scalajs.dom.{MessageEvent, console}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

class Data(var input: Input,
           var isLoading: Boolean,
           var output: ScalaOutput,
           var samples: js.Array[Sample]
          ) extends js.Object {
}

class Input(var source: String, var outputPackage: js.UndefOr[String], var generateFactory: Boolean,
            var forceAbstractFieldOnTrait: Boolean = false) extends js.Object

class ScalaOutput(var text: String, var hasError: Boolean) extends js.Object

class Sample(var url: String, var label: String) extends js.Object

@JSExportTopLevel("Main")
object WebAppMain {
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
      input = new Input(
        source = "",
        outputPackage = "",
        generateFactory = false
      ),
      isLoading = false,
      output = new ScalaOutput(
        text = "",
        hasError = false
      ),
      samples = js.Array(
        new Sample("https://cdn.jsdelivr.net/npm/electron@8.0.0/electron.d.ts", "electron"),
        new Sample("https://cdn.jsdelivr.net/npm/@types/jpm@0.0.5/index.d.ts", "Firefox Addon SDK (jpm)"),
        new Sample("https://raw.githubusercontent.com/DefinitelyTyped/DefinitelyTyped/752136c29dec6f1e559dfc62f6acba08f207c280/types/google-apps-script/google-apps-script.base.d.ts", "Google Apps Script (Base)"),
        new Sample("https://cdn.jsdelivr.net/npm/pixi.js@5.2.1/pixi.js.d.ts", "pixi.js"),
        new Sample("https://cdn.jsdelivr.net/npm/@types/zip.js@2.0.27/index.d.ts", "zip.js"),
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

    def translate(data: Data): Unit = {
      console.info("[Main] send to worker")
      data.isLoading = true
      worker.postMessage(data.input)
    }

    vue = new Vue(js.Dynamic.literal(
      el = "#app",
      data = data,
      methods = js.Dynamic.literal(
        translate = translate _: js.ThisFunction0[Data, _],
        loadSample = js.ThisFunction.fromFunction2 { (data: Data, sample: Sample) =>
          import org.scalajs.dom.experimental._
          data.isLoading = true

          val fetchSampleTypeScript = Fetch.fetch(sample.url).toFuture
          val futureSourceText = fetchSampleTypeScript.flatMap { response =>
            response.text().toFuture.map { text =>
              response.status match {
                case 200 => text
                case _ => s"""Failed to fetch sample.
                             |
                             |Error response:
                             |${text}""".stripMargin
              }
            }
          }
          futureSourceText.foreach { text =>
            data.input.source = text
            translate(data)
          }
        }
      )
    ))
  }
}
