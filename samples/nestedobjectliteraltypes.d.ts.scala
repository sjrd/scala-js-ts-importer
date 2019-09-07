
import scala.scalajs.js
import js.annotation._
import js.|

package nestedobjectliteraltypes {

package A {

trait Info extends js.Object {
  var settings: Info.Settings
}

object Info {

trait Settings extends js.Object {
  var state: Settings.State
}

object Settings {

trait State extends js.Object {
  var enable: Boolean
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
