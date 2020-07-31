import scala.scalajs.js
import js.annotation._
import js.|

package export {

@js.native
trait StringValidator extends js.Object {
  def isAcceptable(s: String): Boolean = js.native
}

package Hoge {

@js.native
@JSGlobal("Hoge.Fuga")
class Fuga extends js.Object {
  var name: String = js.native
}

}

package PIXI {

@js.native
@JSGlobal("PIXI")
object PIXI extends js.Object {
  val VERSION: String = js.native
}

}

package PIXI2 {

@js.native
@JSGlobal("PIXI2")
object PIXI2 extends js.Object {
  val VERSION: String = js.native
}

}

package PIXI3 {

@js.native
@JSGlobal("PIXI3")
object PIXI3 extends js.Object {
  val VERSION: String = js.native
}

}

@js.native
@JSGlobalScope
object Export extends js.Object {
  val numberRegexp: String = js.native
}

}
