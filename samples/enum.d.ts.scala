
import scala.scalajs.js
import js.annotation._
import js.|

package enum {

package enumtype {

@js.native
sealed trait Color extends js.Object {
}

@js.native
@JSGlobal("enumtype.Color")
object Color extends js.Object {
  var Red: Color = js.native
  var Green: Color = js.native
  var Blue: Color = js.native
  @JSBracketAccess
  def apply(value: Color): String = js.native
}

@js.native
sealed trait Button extends js.Object {
}

@js.native
@JSGlobal("enumtype.Button")
object Button extends js.Object {
  var Submit: Button = js.native
  var Reset: Button = js.native
  var Button: Button = js.native
  @JSBracketAccess
  def apply(value: Button): String = js.native
}

@js.native
sealed trait Mixed extends js.Object {
}

@js.native
@JSGlobal("enumtype.Mixed")
object Mixed extends js.Object {
  var EMPTY: Mixed = js.native
  var NUMERIC: Mixed = js.native
  var STRING: Mixed = js.native
  var NEGATIVE: Mixed = js.native
  @JSBracketAccess
  def apply(value: Mixed): String = js.native
}

}

}
