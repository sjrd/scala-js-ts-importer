import scala.scalajs.js
import js.annotation._
import js.|

package source {

trait GPGPUProgram extends js.Object {
  var variableNames: js.Array[String]
  var outputShape: js.Array[Double]
  var params: js.Array[js.Any]
  var userCode: String
  var supportsBroadcasting: js.UndefOr[Boolean]
}

trait Foo extends js.Object {
  var field: js.Array[String]
  def method(): Unit
}

}
