import scala.scalajs.js
import js.annotation._
import js.|

package numberlit {

package numberlit {

@js.native
trait Machine extends js.Object {
  var state: js.UndefOr[Int] = js.native
  def setState(flag: Int | Boolean): Int = js.native
}

@js.native
sealed trait STATUS_FLAGS extends js.Object {
}

@js.native
@JSGlobal("numberlit.STATUS_FLAGS")
object STATUS_FLAGS extends js.Object {
  var NONE: STATUS_FLAGS = js.native
  var DATA_URL: STATUS_FLAGS = js.native
  var COMPLETE: STATUS_FLAGS = js.native
  var LOADING: STATUS_FLAGS = js.native
  @JSBracketAccess
  def apply(value: STATUS_FLAGS): String = js.native
}

@js.native
@JSGlobal("numberlit")
object Numberlit extends js.Object {
  type HttpStatuscode = Int
  def floating(prob: Double): Double | Int = js.native
}

}

}
