/* TypeScript importer for Scala.js
 * Copyright 2013 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package scala.tools.scalajs.tsimporter.sc

import scala.language.implicitConversions

import scala.collection.mutable._

import scala.tools.scalajs.tsimporter.Utils

case class Name(name: String) {
  override def toString() = Utils.scalaEscape(name)
}

object Name {
  val scala = Name("scala")
  val js = Name("js")

  val EMPTY = Name("")
  val CONSTRUCTOR = Name("<init>")
  val REPEATED = Name("*")
}

case class QualifiedName(parts: Name*) {
  def isRoot = parts.isEmpty

  override def toString() =
    if (isRoot) "_root_"
    else parts.mkString(".")

  def dot(name: Name) = QualifiedName((parts :+ name):_*)
  def init = QualifiedName(parts.init:_*)
  def last = parts.last
}

object QualifiedName {
  implicit def fromName(name: Name) = QualifiedName(name)

  val Root = QualifiedName()
  val scala = Root dot Name.scala
  val scala_js = scala dot Name.js

  val Array = scala_js dot Name("Array")
  val FunctionBase = scala_js dot Name("Function")
  def Function(arity: Int) = scala_js dot Name("Function"+arity)
}

class Symbol(val name: Name) {
  override def toString() =
    s"${this.getClass.getSimpleName}($name)}"
}

class CommentSymbol(val text: String) extends Symbol(Name("<comment>")) {
  override def toString() =
    s"/* $text */"
}

class ContainerSymbol(nme: Name) extends Symbol(nme) {
  val members = new ListBuffer[Symbol]

  private var _anonMemberCounter = 0
  def newAnonMemberName() = {
    _anonMemberCounter += 1
    "anon$" + _anonMemberCounter
  }

  def findClass(name: Name): Option[ClassSymbol] = {
    members.collectFirst {
      case sym: ClassSymbol if sym.name == name => sym
    }
  }

  def findModule(name: Name): Option[ModuleSymbol] = {
    members.collectFirst {
      case sym: ModuleSymbol if sym.name == name => sym
    }
  }

  def getClassOrCreate(name: Name): ClassSymbol = {
    findClass(name) getOrElse {
      val result = new ClassSymbol(name)
      members += result
      findModule(name) foreach { companion =>
        result.companionModule = companion
        companion.companionClass = result
      }
      result
    }
  }

  def getModuleOrCreate(name: Name): ModuleSymbol = {
    findModule(name) getOrElse {
      val result = new ModuleSymbol(name)
      members += result
      findClass(name) foreach { companion =>
        result.companionClass = companion
        companion.companionModule = result
      }
      result
    }
  }

  def newField(name: Name): FieldSymbol = {
    val result = new FieldSymbol(name)
    members += result
    result
  }

  def newMethod(name: Name): MethodSymbol = {
    val result = new MethodSymbol(name)
    members += result
    result
  }
}

class PackageSymbol(nme: Name) extends ContainerSymbol(nme) {
  override def toString() = s"package $name"

  def findPackage(name: Name): Option[PackageSymbol] = {
    members.collectFirst {
      case sym: PackageSymbol if sym.name == name => sym
    }
  }

  def getPackageOrCreate(name: Name): PackageSymbol = {
    findPackage(name) getOrElse {
      val result = new PackageSymbol(name)
      members += result
      result
    }
  }
}

class ClassSymbol(nme: Name) extends ContainerSymbol(nme) {
  val tparams = new ListBuffer[Name]
  val parents = new ListBuffer[TypeRef]
  var companionModule: ModuleSymbol = _
  var isTrait: Boolean = true

  override def toString() = (
      (if (isTrait) s"trait $name" else s"class $name") +
      (if (tparams.isEmpty) "" else tparams.mkString("<", ", ", ">")))
}

class ModuleSymbol(nme: Name) extends ContainerSymbol(nme) {
  var companionClass: ClassSymbol = _

  override def toString() = s"object $name"
}

class FieldSymbol(nme: Name) extends Symbol(nme) {
  var tpe: TypeRef = TypeRef.Any

  override def toString() = s"var $name: $tpe"
}

class MethodSymbol(nme: Name) extends Symbol(nme) {
  val tparams = new ListBuffer[Name]
  val params = new ListBuffer[ParamSymbol]
  var resultType: TypeRef = TypeRef.Dynamic

  override def toString() = {
    val tparamsStr =
      if (tparams.isEmpty) ""
      else tparams.mkString("[", ", ", "]")
    s"def $name$tparamsStr(${params.mkString(", ")}): $resultType"
  }
}

class ParamSymbol(nme: Name) extends Symbol(nme) {
  var optional: Boolean = false
  var tpe: TypeRef = TypeRef.Any

  override def toString() =
    s"$name: $tpe" + (if (optional) " = _" else "")
}

case class TypeRef(typeName: QualifiedName, targs: List[TypeRef] = Nil) {
  override def toString() =
    s"$typeName[${targs.mkString(", ")}]"
}

object TypeRef {
  import QualifiedName.{ scala, scala_js }

  val Any = TypeRef(scala_js dot Name("Any"))
  val Dynamic = TypeRef(scala_js dot Name("Dynamic"))
  val Number = TypeRef(scala_js dot Name("Number"))
  val Boolean = TypeRef(scala_js dot Name("Boolean"))
  val String = TypeRef(scala_js dot Name("String"))
  val Object = TypeRef(scala_js dot Name("Object"))
  val Function = TypeRef(scala_js dot Name("Function"))
  val Unit = TypeRef(scala dot Name("Unit"))

  object Repeated {
    def apply(underlying: TypeRef): TypeRef =
      TypeRef(QualifiedName(Name.REPEATED), List(underlying))

    def unapply(typeRef: TypeRef) = typeRef match {
      case TypeRef(QualifiedName(Name.REPEATED), List(underlying)) =>
        Some(underlying)

      case _ => None
    }
  }
}
