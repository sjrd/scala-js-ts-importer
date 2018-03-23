
import scala.scalajs.js
import js.annotation._
import js.|

package indexabletypes {

@js.native
trait NumberDictionary extends js.Object {
  @JSBracketAccess
  def apply(index: String): Double = js.native
  @JSBracketAccess
  def update(index: String, v: Double): Unit = js.native
}

@js.native
trait ReadonlyStringArray extends js.Object {
  @JSBracketAccess
  def apply(index: Double): String = js.native
}

}
