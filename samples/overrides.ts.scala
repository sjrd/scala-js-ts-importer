
import scala.scalajs.js
import js.annotation._
import js.|

package overrides {

package overrides {

@js.native
@JSGlobal("overrides.A")
class A extends js.Object {
  def equals(other: A): Boolean = js.native
  override def clone(): A = js.native
  override def toString(): String = js.native
}

@js.native
trait BLike extends js.Object {
  override def toString(): String = js.native
}

@js.native
@JSGlobal("overrides.B")
class B extends BLike {
  def equals(other: js.Any): Boolean = js.native
  override def clone(): BLike = js.native
  override def toString(): String = js.native
}

@js.native
trait C extends js.Object {
  def equals(other: js.Any): Boolean = js.native
  override def clone(): C = js.native
  override def toString(): String = js.native
}

}

}
