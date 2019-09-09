
import scala.scalajs.js
import js.annotation._
import js.|

package comma {

@js.native
@JSGlobal
class Foo extends js.Object {
  def foo(options: js.Any, key3: String): Unit = js.native
}

@js.native
trait Bar extends js.Object {
  var key1: String = js.native
  var key2: String = js.native
}

@js.native
@JSGlobalScope
object Comma extends js.Object {
  type Callback[R] = js.Function1[R, Unit]
  type Handler[T, R] = js.Function2[T, Callback[R], Unit]
}

}
