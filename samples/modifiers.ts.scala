
import scala.scalajs.js
import js.annotation._
import js.|

package importedjs {

package monaco {

@js.native
trait IEvent[T] extends js.Object {
}

@js.native
@JSGlobal("monaco.Emitter")
class Emitter[T] extends js.Object {
  def event: IEvent[T] = js.native
  def fire(event: T = ???): Unit = js.native
  def dispose(): Unit = js.native
}

@js.native
@JSGlobal("monaco.EditorType")
object EditorType extends js.Object {
  var ICodeEditor: String = js.native
  var IDiffEditor: String = js.native
}

@js.native
@JSGlobal("monaco.Uri")
class Uri extends js.Object {
  def scheme: String = js.native
  def authority: String = js.native
  def path: String = js.native
}

@js.native
@JSGlobal("monaco.Uri")
object Uri extends js.Object {
  def isUri(thing: js.Any): Boolean = js.native
  def parse(value: String): Uri = js.native
}

@js.native
@JSGlobal("monaco")
object Monaco extends js.Object {
  val id: String = js.native
  val CursorMoveByUnit: js.Any = js.native
}

}

}
