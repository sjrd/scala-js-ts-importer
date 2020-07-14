import scala.scalajs.js
import js.annotation._
import js.|

package source {

@js.native
sealed trait CodeBuildStateType extends js.Any {
}

object CodeBuildStateType {
  @inline val IN_PROGRESS: CodeBuildStateType = "IN_PROGRESS".asInstanceOf[CodeBuildStateType]
  @inline val SUCCEEDED: CodeBuildStateType = "SUCCEEDED".asInstanceOf[CodeBuildStateType]
}

@js.native
sealed trait ABC extends js.Any {
}

object ABC {
  @inline val A: ABC = "A".asInstanceOf[ABC]
  @inline val B: ABC = "B".asInstanceOf[ABC]
  @inline val C: ABC = "C".asInstanceOf[ABC]
}

@js.native
sealed trait Mixed extends js.Any {
}

object Mixed {
  @inline val `1`: Mixed = 1.asInstanceOf[Mixed]
  @inline val `2`: Mixed = 2.asInstanceOf[Mixed]
  @inline val three: Mixed = "three".asInstanceOf[Mixed]
  @inline val `4.4`: Mixed = 4.4.asInstanceOf[Mixed]
  @inline val `true`: Mixed = true.asInstanceOf[Mixed]
  @inline val `false`: Mixed = false.asInstanceOf[Mixed]
}

}
