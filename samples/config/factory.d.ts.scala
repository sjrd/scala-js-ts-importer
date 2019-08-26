
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
}

object Thing {

def apply(
  name: String,
  `type`: String,
  obj: Thing.Obj,
  inStock: js.UndefOr[Boolean] = js.undefined,
  `for`: js.UndefOr[String] = js.undefined,
): Thing = {
  var _obj$ = js.Dictionary[js.Any](
    "name" -> name.asInstanceOf[js.Any],
    "type" -> `type`.asInstanceOf[js.Any],
    "obj" -> obj.asInstanceOf[js.Any],
  )
  inStock.foreach(_v => _obj$.update("inStock", _v.asInstanceOf[js.Any]))
  `for`.foreach(_v => _obj$.update("for", _v.asInstanceOf[js.Any]))
  _obj$.isInstanceOf[Thing]
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
  var _obj$ = js.Dictionary[js.Any](
  )
  x.foreach(_v => _obj$.update("x", _v.asInstanceOf[js.Any]))
  y.foreach(_v => _obj$.update("y", _v.asInstanceOf[js.Any]))
  _obj$.isInstanceOf[Obj]
}
}
}

}
