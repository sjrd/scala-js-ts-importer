
import scala.scalajs.js
import js.annotation._
import js.|

package `interface-variables` {

@js.native
trait GPGPUProgram extends js.Object {
  var variableNames: js.Array[String] = js.native
  var outputShape: js.Array[Double] = js.native
  var params: js.Array[js.Any] = js.native
  var userCode: String = js.native
  var supportsBroadcasting: Boolean = js.native
}

}