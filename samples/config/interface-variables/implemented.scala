import scala.scalajs.js
import js.annotation._
import js.|

package source {

@js.native
trait GPGPUProgram extends js.Object {
  var variableNames: js.Array[String] = js.native
  var outputShape: js.Array[Double] = js.native
  var params: js.Array[js.Any] = js.native
  var userCode: String = js.native
  var supportsBroadcasting: js.UndefOr[Boolean] = js.native
}

@js.native
trait Foo extends js.Object {
  var field: js.Array[String] = js.native
  def method(): Unit = js.native
}

}
