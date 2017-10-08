
import scala.scalajs.js
import js.annotation._
import js.|

package importedjs {

package nevertype {

@js.native
@JSGlobal("nevertype.ValueTermQueryBase")
class ValueTermQueryBase extends js.Object {
  var never: Nothing = js.native
  def value(queryVal: String | Double): String = js.native
  def method(foo: Nothing): js.Array[Nothing] = js.native
}

@js.native
@JSGlobal("nevertype.RangeQuery")
class RangeQuery extends ValueTermQueryBase {
  def value(queryVal: String | Double): Nothing = js.native
}

}

}
