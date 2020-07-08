package org.scalajs.tools.tsimporter

import org.scalajs.dom.webworkers.Worker
import org.scalajs.dom.{ MessageEvent, console }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.{ JSExport, JSExportTopLevel }

class Data(var input: Input,
           var isLoading: Boolean,
           var output: ScalaOutput,
           var samples: js.Array[Sample]
          ) extends js.Object {
}

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
        generateFactory = GenerateFactoryType.generate,
        interfaceImplementation = InterfaceImplementation.`abstract`,
      ),
      isLoading = false,
      output = new ScalaOutput(
        text = "",
        hasError = false
      ),
      samples = js.Array(
        new Sample("https://cdn.jsdelivr.net/npm/electron@9.1.0/electron.d.ts", "electron"),
        new Sample("https://cdn.jsdelivr.net/npm/@types/jpm@0.0.5/index.d.ts", "Firefox Addon SDK (jpm)"),
        new Sample("https://raw.githubusercontent.com/DefinitelyTyped/DefinitelyTyped/a92d898478072a57dabc21308f7d49698b5c1be8/types/google-apps-script/google-apps-script.base.d.ts", "Google Apps Script (Base)"),
        new Sample("https://cdn.jsdelivr.net/npm/pixi.js@5.3.0/pixi.js.d.ts", "pixi.js"),
        new Sample("https://cdn.jsdelivr.net/npm/@types/zip.js@2.0.28/index.d.ts", "zip.js"),
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

    vue = new Vue(new VueOptions(
      el = "#app",
      data = data,
      mounted = () => {
        // TODO: Use Semantic-UI-Vue
        js.Dynamic.global.jQuery(".popup-help").popup()
      },
      watch = js.Dictionary(
        "input.outputPackage" -> (() => translate(data)),
        "input.generateFactory" -> (() => translate(data)),
        "input.interfaceImplementation" -> (() => translate(data)),
      ),
      methods = js.Dictionary(
        "translate" -> js.ThisFunction.fromFunction1(translate),
        "loadSample" -> js.ThisFunction.fromFunction2 { (data: Data, sample: Sample) =>
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
