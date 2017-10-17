
import scala.scalajs.js
import js.annotation._
import js.|

package intersectiontype {

package intersectiontype {

@js.native
trait CoreOptions extends js.Object {
  var statConcurrency: Double = js.native
}

@js.native
trait ExtraOptions extends js.Object {
  var allowHalfOpen: Boolean = js.native
}

@js.native
trait MoreExtraOptions extends js.Object {
  var store: Boolean = js.native
}

@js.native
@JSGlobal("intersectiontype")
object Intersectiontype extends js.Object {
  type ArchiverOptions = CoreOptions with ExtraOptions with MoreExtraOptions
  type UnionOfIntersection = CoreOptions | CoreOptions with ExtraOptions | CoreOptions with MoreExtraOptions
  type Duplicates = CoreOptions with ExtraOptions with MoreExtraOptions
  def test(v: CoreOptions with ExtraOptions): CoreOptions with MoreExtraOptions = js.native
}

}

}
