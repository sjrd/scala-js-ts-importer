
import scala.scalajs.js
import js.annotation._
import js.|

package valueExpression {

package valueExpression {

@js.native
sealed trait StringEnum extends js.Object {
}

@js.native
@JSGlobal("valueExpression.StringEnum")
object StringEnum extends js.Object {
  var A: StringEnum = js.native
  var B: StringEnum = js.native
  var C: StringEnum = js.native
  @JSBracketAccess
  def apply(value: StringEnum): String = js.native
}

@js.native
sealed trait NumberEnum extends js.Object {
}

@js.native
@JSGlobal("valueExpression.NumberEnum")
object NumberEnum extends js.Object {
  var A: NumberEnum = js.native
  var B: NumberEnum = js.native
  var C: NumberEnum = js.native
  @JSBracketAccess
  def apply(value: NumberEnum): String = js.native
}

}

}
