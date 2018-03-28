
import scala.scalajs.js
import js.annotation._
import js.|

package extendsintersection {

package M {

@js.native
trait A extends js.Object {
}

@js.native
trait B extends js.Object {
}

@js.native
@JSGlobal("M")
object M extends js.Object {
  def f[T <: A with B](t: T): js.Dynamic = js.native
}

}

}
