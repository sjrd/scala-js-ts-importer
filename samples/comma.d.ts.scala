import scala.scalajs.js
import js.annotation._
import js.|

package comma {

@js.native
@JSGlobal
class Foo extends js.Object {
  def foo(options: FooOptions, key3: String): Unit = js.native
}

@js.native
trait FooOptions extends js.Object {
  var key1: String = js.native
  var key2: String = js.native
}

@js.native
trait Bar extends js.Object {
  var key1: String = js.native
  var key2: String = js.native
}

}
