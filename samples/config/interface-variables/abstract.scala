
import scala.scalajs.js
import js.annotation._
import js.|

package source {

trait GPGPUProgram extends js.Object {
  var variableNames: js.Array[String]
  var outputShape: js.Array[Double]
  var params: js.Array[js.Any]
  var userCode: String
  var supportsBroadcasting: Boolean
}

}
