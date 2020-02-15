package org.scalajs.tools.tsimporter

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("vue", "Vue", "Vue")
class Vue(options: VueOptions) extends js.Object  {

}


class VueOptions(
  val el: String,
  val data: js.Object,
  val mounted: js.Function,
  val methods: js.Dictionary[js.Function]              
) extends js.Object
