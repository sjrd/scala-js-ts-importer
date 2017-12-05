
import scala.scalajs.js
import js.annotation._
import js.|

package nestedobjectliteraltypes {

package A {

@js.native
trait Info extends js.Object {
  var settings: Info.Settings = js.native
}

object Info {

@js.native
trait Settings extends js.Object {
  var state: Settings.State = js.native
}

object Settings {

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
