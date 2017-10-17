
import scala.scalajs.js
import js.annotation._
import js.|

package numberlit {

package numberlit {

@js.native
trait Machine extends js.Object {
  var state: Int = js.native
  def setState(flag: Int | Boolean): Int = js.native
}

@js.native
@JSGlobal("numberlit")
object Numberlit extends js.Object {
  type HttpStatuscode = Int
  def floating(prob: Double): Double | Int = js.native
}

}

}
