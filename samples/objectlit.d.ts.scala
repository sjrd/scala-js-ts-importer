
import scala.scalajs.js
import js.annotation._
import js.|

package objectlit {

@js.native
@JSGlobal
object ObjectType extends js.Object {
  var name: String = js.native
  var age: Double = js.native
}

@js.native
@JSGlobal
object NumericKeyObjectType extends js.Object {
  var `0`: Double = js.native
  var `1`: Double = js.native
  var `2.1`: Double = js.native
}

}
