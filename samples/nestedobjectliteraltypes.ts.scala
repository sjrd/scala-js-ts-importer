
import scala.scalajs.js
import js.annotation._
import js.|

package nestedobjectliteraltypes {

package A {

@js.native
trait Info extends js.Object {
  var settings: `anon$1` = js.native
}

@js.native
trait `anon$1` extends js.Object {
  var state: `anon$2` = js.native
}

@js.native
trait `anon$2` extends js.Object {
  var enable: Boolean = js.native
}

@js.native
@JSGlobal("A")
object A extends js.Object {
  def objectInfo: Info = js.native
}

}

}
