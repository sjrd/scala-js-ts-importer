/* TypeScript importer for Scala.js
 * Copyright 2013-2014 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package org.scalajs.tools.tsimporter.sc

import scala.language.implicitConversions

import scala.collection.mutable._

import org.scalajs.tools.tsimporter.Utils

case class Name(name: String) {
  override def toString() = Utils.scalaEscape(name)
}

object Name {
  val scala = Name("scala")
  val scalajs = Name("scalajs")
  val js = Name("js")
  val java = Name("java")
  val lang = Name("lang")

  val EMPTY = Name("")
  val CONSTRUCTOR = Name("<init>")
  val REPEATED = Name("*")
  val SINGLETON = Name("<typeof>")
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
  val scala_js = scala dot Name.scalajs dot Name.js
  val java_lang = Root dot Name.java dot Name.lang

  val Array = scala_js dot Name("Array")
  val Dictionary = scala_js dot Name("Dictionary")
  val FunctionBase = scala_js dot Name("Function")
  def Function(arity: Int) = scala_js dot Name("Function"+arity)
  def Tuple(arity: Int) = scala_js dot Name("Tuple"+arity)
  val Union = scala_js dot Name("|")
}

class Symbol(val name: Name) {
  override def toString() =
    s"${this.getClass.getSimpleName}($name)}"
}

trait JSNameable extends Symbol {
  var jsName: Option[String] = None

  def protectName(): Unit = {
    val n = name.name
    if (jsName.isEmpty && (n.contains("$") || n == "apply"))
      jsName = Some(n)
  }

  protected def jsNameStr =
    jsName.fold("")(n => s"""@JSName("$n") """)
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

  def newTypeAlias(name: Name): TypeAliasSymbol = {
    val result = new TypeAliasSymbol(name)
    members += result
    result
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

  def removeIfDuplicate(sym: MethodSymbol): Unit = {
    val isDuplicate = members.exists(s => (s ne sym) && (s == sym))
    if (isDuplicate)
      members.remove(members.indexWhere(_ eq sym))
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
  val tparams = new ListBuffer[TypeParamSymbol]
  val parents = new ListBuffer[TypeRef]
  var companionModule: ModuleSymbol = _
  var isTrait: Boolean = true
  var isSealed: Boolean = false

  override def toString() = (
      (if (isSealed) "sealed " else "") +
      (if (isTrait) s"trait $name" else s"class $name") +
      (if (tparams.isEmpty) "" else tparams.mkString("<", ", ", ">")))
}

class ModuleSymbol(nme: Name) extends ContainerSymbol(nme) {
  var companionClass: ClassSymbol = _

  override def toString() = s"object $name"
}

class TypeAliasSymbol(nme: Name) extends Symbol(nme) {
  val tparams = new ListBuffer[TypeParamSymbol]
  var alias: TypeRef = TypeRef.Any

  override def toString() = (
      (s"type $name") +
      (if (tparams.isEmpty) "" else tparams.mkString("<", ", ", ">")))
}

class FieldSymbol(nme: Name) extends Symbol(nme) with JSNameable {
  var tpe: TypeRef = TypeRef.Any

  override def toString() = s"${jsNameStr}var $name: $tpe"
}

class MethodSymbol(nme: Name) extends Symbol(nme) with JSNameable {
  val tparams = new ListBuffer[TypeParamSymbol]
  val params = new ListBuffer[ParamSymbol]
  var resultType: TypeRef = TypeRef.Dynamic

  var isBracketAccess: Boolean = false

  override def toString() = {
    val bracketAccessStr =
      if (isBracketAccess) "@JSBracketAccess " else ""
    val tparamsStr =
      if (tparams.isEmpty) ""
      else tparams.mkString("[", ", ", "]")
    s"${jsNameStr}${bracketAccessStr}def $name$tparamsStr(${params.mkString(", ")}): $resultType"
  }

  def paramTypes = params.map(_.tpe)

  override def equals(that: Any): Boolean = that match {
    case that: MethodSymbol =>
      (this.name == that.name &&
          this.tparams == that.tparams &&
          this.paramTypes == that.paramTypes &&
          this.resultType == that.resultType)
    case _ =>
      false
  }
}

class TypeParamSymbol(nme: Name, val upperBound: Option[TypeRef]) extends Symbol(nme) {
  override def toString() = {
    nme.toString + upperBound.fold("")(bound => s" <: $bound")
  }

  override def equals(that: Any): Boolean = that match {
    case that: TypeParamSymbol =>
      (this.name == that.name &&
          this.upperBound == that.upperBound)
    case _ =>
      false
  }
}

class ParamSymbol(nme: Name) extends Symbol(nme) {
  def this(nme: Name, tpe: TypeRef) = {
    this(nme)
    this.tpe = tpe
  }

  var optional: Boolean = false
  var tpe: TypeRef = TypeRef.Any

  override def toString() =
    s"$name: $tpe" + (if (optional) " = _" else "")

  override def equals(that: Any): Boolean = that match {
    case that: ParamSymbol =>
      (this.name == that.name &&
          this.tpe == that.tpe)
    case _ =>
      false
  }
}

case class TypeRef(typeName: QualifiedName, targs: List[TypeRef] = Nil) {
  override def toString() =
    s"$typeName[${targs.mkString(", ")}]"
}

object TypeRef {
  import QualifiedName.{ scala, scala_js, java_lang }

  val ScalaAny = TypeRef(scala dot Name("Any"))

  val Any = TypeRef(scala_js dot Name("Any"))
  val Dynamic = TypeRef(scala_js dot Name("Dynamic"))
  val Double = TypeRef(scala dot Name("Double"))
  val Boolean = TypeRef(scala dot Name("Boolean"))
  val String = TypeRef(java_lang dot Name("String"))
  val Object = TypeRef(scala_js dot Name("Object"))
  val Function = TypeRef(scala_js dot Name("Function"))
  val Unit = TypeRef(scala dot Name("Unit"))
  val Null = TypeRef(scala dot Name("Null"))

  object Union {
    def apply(left: TypeRef, right: TypeRef): TypeRef =
      TypeRef(QualifiedName.Union, List(left, right))

    def unapply(typeRef: TypeRef): Option[(TypeRef, TypeRef)] = typeRef match {
      case TypeRef(QualifiedName.Union, List(left, right)) =>
        Some((left, right))

      case _ => None
    }
  }

  object Singleton {
    def apply(underlying: QualifiedName): TypeRef =
      TypeRef(QualifiedName(Name.SINGLETON), List(TypeRef(underlying)))

    def unapply(typeRef: TypeRef): Option[QualifiedName] = typeRef match {
      case TypeRef(QualifiedName(Name.SINGLETON), List(TypeRef(underlying, Nil))) =>
        Some(underlying)

      case _ => None
    }
  }

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
