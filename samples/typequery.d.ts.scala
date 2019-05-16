
import scala.scalajs.js
import js.annotation._
import js.|

package typequery {

package typequery {

@js.native
trait C extends js.Object {
  def `catch`[T](ex: T): Unit = js.native
  def delete[T](array: js.Array[T], key: Double): Unit = js.native
}

@js.native
trait D extends js.Object {
  var x: Any /* X.type */ = js.native
  var cat: Any /* X.`catch`.type */ = js.native
  var del: Any /* X.delete.type */ = js.native
}

@js.native
@JSGlobal("typequery")
object Typequery extends js.Object {
  val X: C = js.native
}

}

}
