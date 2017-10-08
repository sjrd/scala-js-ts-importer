
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

}

}
