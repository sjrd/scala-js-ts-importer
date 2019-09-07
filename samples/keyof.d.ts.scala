
import scala.scalajs.js
import js.annotation._
import js.|

package keyof {

trait Thing extends js.Object {
  var name: String
  var width: Double
  var height: Double
  var inStock: Boolean
}

@js.native
trait LoDashStatic extends js.Object {
  def at[T](`object`: T | Null | Unit, props: String*): js.Array[js.Any] = js.native
}

@js.native
@JSGlobalScope
object Keyof extends js.Object {
  type K1 = String
  type K2 = String
  type K3 = String
  type P1 = js.Any
  type P2 = js.Any
  type P3 = js.Any
  type P4 = js.Any
  type P5 = js.Any
}

}
