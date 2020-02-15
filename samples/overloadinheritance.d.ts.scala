import scala.scalajs.js
import js.annotation._
import js.|

package overloadinheritance {

@js.native
@JSGlobal
class Parent extends js.Object {
  def foo(): Unit = js.native
  def bar(a: String): Unit = js.native
  var width: Double = js.native
  def x: Double = js.native
}

@js.native
@JSGlobal
class Child extends Parent {
  override def foo(): Unit = js.native
  override def x: Double = js.native
}

}
