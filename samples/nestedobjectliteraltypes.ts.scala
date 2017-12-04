
import scala.scalajs.js
import js.annotation._
import js.|

package nestedobjectliteraltypes {

package A {

@js.native
trait Info extends js.Object {
  var settings: Info.settings = js.native
}

@js.native
object Info extends js.Object {

@js.native
trait settings extends js.Object {
  var state: settings.state = js.native
}

@js.native
object settings extends js.Object {

@js.native
trait state extends js.Object {
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
