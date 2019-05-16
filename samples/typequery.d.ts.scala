
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
  var c: C = js.native
  var cat: Any /* C.`catch` */ = js.native
  var del: Any /* C.delete */ = js.native
}

@js.native
@JSGlobal("typequery")
object Typequery extends js.Object {
  val C: C = js.native
}

}

}
