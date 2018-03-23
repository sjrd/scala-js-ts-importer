
import scala.scalajs.js
import js.annotation._
import js.|

package generics {

package generics {

@js.native
@JSGlobal("generics.Thing")
class Thing extends js.Object {
  var name: String = js.native
}

@js.native
@JSGlobal("generics.Container")
class Container[T] extends js.Object {
  var t: T = js.native
}

@js.native
@JSGlobal("generics.ContainerWithUpperBound")
class ContainerWithUpperBound[T <: Thing] extends js.Object {
  var t: T = js.native
}

@js.native
@JSGlobal("generics.ContainerWithDefault")
class ContainerWithDefault[T] extends js.Object {
  var t: T = js.native
}

@js.native
@JSGlobal("generics.ContainerWithUpperBoundAndDefault")
class ContainerWithUpperBoundAndDefault[T <: Thing] extends js.Object {
  var t: T = js.native
}

}

}
