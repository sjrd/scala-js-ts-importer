
import scala.scalajs.js
import js.annotation._
import js.|

package `then` {

package `then` {

@js.native
trait Thenable[T] extends js.Object {
  def `then`[TResult](onfulfilled: js.Function1[T, TResult | Thenable[TResult]] = ???, onrejected: js.Function1[js.Any, TResult | Thenable[TResult]] = ???): Thenable[TResult] = js.native
}

@js.native
@JSGlobal("then.then")
class `then` extends js.Object {
}

}

}
