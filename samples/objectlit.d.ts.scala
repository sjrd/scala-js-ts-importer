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

@js.native
trait IPluginObject extends js.Object {
  var name: String = js.native
  def sum(a: Double, b: Double): Double = js.native
}

@js.native
trait IPlugin2Object[T] extends js.Object {
  var name: T = js.native
}

@js.native
trait AreaOptions extends js.Object {
  var width: Double = js.native
  var height: Double = js.native
}

@js.native
@JSGlobalScope
object Objectlit extends js.Object {
  type IPlugin = IPluginObject
  type IPlugin2[T] = IPlugin2Object[T]
  def area(options: AreaOptions): Double = js.native
}

}
