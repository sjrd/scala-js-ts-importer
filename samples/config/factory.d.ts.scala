
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

def apply(
  name: String,
  `type`: String,
  obj: Thing.Obj,
  foo: String | Null = null,
  inStock: js.UndefOr[Boolean] = js.undefined,
  `for`: js.UndefOr[String] = js.undefined,
): Thing = {
  val _obj$ = js.Dynamic.literal(
    "name" -> name.asInstanceOf[js.Any],
    "type" -> `type`.asInstanceOf[js.Any],
    "obj" -> obj.asInstanceOf[js.Any],
    "foo" -> foo.asInstanceOf[js.Any],
  )
  inStock.foreach(_v => _obj$.updateDynamic("inStock")(_v.asInstanceOf[js.Any]))
  `for`.foreach(_v => _obj$.updateDynamic("for")(_v.asInstanceOf[js.Any]))
  _obj$.asInstanceOf[Thing]
}

@js.native
trait Obj extends js.Object {
  var x: js.UndefOr[Double] = js.native
  var y: js.UndefOr[Double] = js.native
}

object Obj {

def apply(
  x: js.UndefOr[Double] = js.undefined,
  y: js.UndefOr[Double] = js.undefined,
): Obj = {
  val _obj$ = js.Dynamic.literal(
  )
  x.foreach(_v => _obj$.updateDynamic("x")(_v.asInstanceOf[js.Any]))
  y.foreach(_v => _obj$.updateDynamic("y")(_v.asInstanceOf[js.Any]))
  _obj$.asInstanceOf[Obj]
}
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
