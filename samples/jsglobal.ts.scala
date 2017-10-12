
import scala.scalajs.js
import js.annotation._
import js.|

package jsglobal {

@js.native
@JSGlobal
class Point protected () extends js.Object {
  def this(x: Double, y: Double) = this()
  def x: Double = js.native
  def y: Double = js.native
}

@js.native
@JSGlobal
object Point extends js.Object {
  def isPoint(thing: js.Any): Boolean = js.native
}

package nested {

@js.native
@JSGlobal("nested.Circle")
class Circle protected () extends js.Object {
  def this(center: Point, radius: Double) = this()
  def center: Point = js.native
  def radius: Double = js.native
}

@js.native
@JSGlobal("nested.Circle")
object Circle extends js.Object {
  def isCirce(thing: js.Any): Boolean = js.native
}

@js.native
@JSGlobal("nested")
object Nested extends js.Object {
  type Line = js.Array[Point]
}

}

@js.native
@JSGlobalScope
object Jsglobal extends js.Object {
  val globalConst: String = js.native
  def globalVar: String = js.native
  def globalFunc(): String = js.native
}

}
