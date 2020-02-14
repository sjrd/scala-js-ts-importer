import scala.scalajs.js
import js.annotation._
import js.|

package factory {

@js.native
trait Thing extends js.Object {
  var name: String = js.native
  var `type`: String = js.native
  var obj: Thing.Obj = js.native
  var inStock: js.UndefOr[Boolean] = js.native
  var `for`: js.UndefOr[String] = js.native
  var foo: String | Null = js.native
}

object Thing {

@js.native
trait Obj extends js.Object {
  var x: js.UndefOr[Double] = js.native
  var y: js.UndefOr[Double] = js.native
}
}

@js.native
trait MethodOnly extends js.Object {
  def method(s: String): Unit = js.native
}

@js.native
trait MethodAndProperty extends js.Object {
  var prop: String = js.native
  def method(s: String): Unit = js.native
}

}
