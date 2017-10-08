
import scala.scalajs.js
import js.annotation._
import js.|

package importedjs {

package nevertype {

@js.native
@JSGlobal("nevertype.ValueTermQueryBase")
class ValueTermQueryBase extends js.Object {
  def value(queryVal: String | Double): String = js.native
}

@js.native
@JSGlobal("nevertype.RangeQuery")
class RangeQuery extends ValueTermQueryBase {
  def value(queryVal: String | Double): Nothing = js.native
}

@js.native
@JSGlobal("nevertype.RangeQuery2")
class RangeQuery2 extends ValueTermQueryBase {
  def value(): Nothing = js.native
}

}

}
