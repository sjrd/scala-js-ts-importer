
import scala.scalajs.js
import js.annotation._
import js.|

package `import` {

package mod {

@js.native
@JSGlobal("mod")
object Mod extends js.Object {
  def f(x: Number): String = js.native
}

}

@js.native
@JSGlobalScope
object Import extends js.Object {
  val hello: String = js.native
}

}
