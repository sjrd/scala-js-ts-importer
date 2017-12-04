
import scala.scalajs.js
import js.annotation._
import js.|

package nestedobjectliteraltypes {

package A {

@js.native
trait Info extends js.Object {
  var settings: Info.Settings = js.native
}

@js.native
object Info extends js.Object {

@js.native
trait Settings extends js.Object {
  var state: Settings.State = js.native
}

@js.native
object Settings extends js.Object {

@js.native
trait State extends js.Object {
  var enable: Boolean = js.native
}
}
}

@js.native
@JSGlobal("A")
object A extends js.Object {
  def objectInfo: Info = js.native
}

}

}
