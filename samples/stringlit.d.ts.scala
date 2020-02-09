import scala.scalajs.js
import js.annotation._
import js.|

package stringlit {

package stringlit {

@js.native
trait IEditorOptions extends js.Object {
  var ariaLabel: js.UndefOr[String] = js.native
  var rulers: js.UndefOr[js.Array[Double]] = js.native
  var selectionClipboard: js.UndefOr[Boolean] = js.native
  var lineNumbers: js.UndefOr[String | js.Function1[Double, String]] = js.native
  var readable: js.UndefOr[String | Boolean] = js.native
}

@js.native
@JSGlobal("stringlit")
object Stringlit extends js.Object {
  type BuiltinTheme = String
}

}

}
