
import scala.scalajs.js
import js.annotation._
import js.|

package stringlit {

package stringlit {

@js.native
trait IEditorOptions extends js.Object {
  var ariaLabel: String = js.native
  var rulers: js.Array[Double] = js.native
  var selectionClipboard: Boolean = js.native
  var lineNumbers: String | js.Function1[Double, String] = js.native
  var readable: String | Boolean = js.native
}

@js.native
@JSGlobal("stringlit")
object Stringlit extends js.Object {
  type BuiltinTheme = String
}

}

}
