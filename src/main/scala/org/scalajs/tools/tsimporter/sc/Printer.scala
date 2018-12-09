/* TypeScript importer for Scala.js
 * Copyright 2013-2014 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package org.scalajs.tools.tsimporter.sc

import java.io.PrintWriter
import org.scalajs.tools.tsimporter.Trees.Modifier

class Printer(private val output: PrintWriter, outputPackage: String) {
  import Printer._

  private implicit val self = this

  private var currentJSNamespace = ""

  def printSymbol(sym: Symbol) {
    val name = sym.name
    sym match {
      case comment: CommentSymbol =>
        pln"/* ${comment.text} */"

      case sym: PackageSymbol =>
        val isRootPackage = name == Name.EMPTY

        val parentPackage :+ thisPackage =
          if (isRootPackage) outputPackage.split("\\.").toList.map(Name(_))
          else List(name)

        if (!parentPackage.isEmpty) {
          pln"package ${parentPackage.mkString(".")}"
        }

        if (isRootPackage) {
          pln"";
          pln"import scala.scalajs.js"
          pln"import js.annotation._"
          pln"import js.|"
        }

        val oldJSNamespace = currentJSNamespace
        if (!isRootPackage)
          currentJSNamespace += name.name + "."

        if (!sym.members.isEmpty) {
          val (topLevels, packageObjectMembers) =
            sym.members.partition(canBeTopLevel)

          pln"";
          pln"package $thisPackage {"

          for (sym <- topLevels)
            printSymbol(sym)

          if (!packageObjectMembers.isEmpty) {
            val packageObjectName =
              Name(thisPackage.name.head.toUpper + thisPackage.name.tail)

            pln"";
            if (currentJSNamespace.isEmpty) {
              pln"@js.native"
              pln"@JSGlobalScope"
              pln"object $packageObjectName extends js.Object {"
            } else {
              val jsName = currentJSNamespace.init
              pln"@js.native"
              pln"""@JSGlobal("$jsName")"""
              pln"object $packageObjectName extends js.Object {"
            }
            for (sym <- packageObjectMembers)
              printSymbol(sym)
            pln"}"
          }

          pln"";
          pln"}"
        }

        currentJSNamespace = oldJSNamespace

      case sym: ClassSymbol =>
        val sealedKw = if (sym.isSealed) "sealed " else ""
        val abstractKw = if (sym.isAbstract) "abstract " else ""
        val kw = if (sym.isTrait) "trait" else "class"
        val constructorStr =
          if (sym.isTrait) ""
          else if (sym.members.exists(isParameterlessConstructor)) ""
          else " protected ()"
        val parents =
          if (sym.parents.isEmpty) List(TypeRef.Object)
          else sym.parents.toList

        pln"";
        pln"@js.native"
        if (!sym.isTrait) {
          if (currentJSNamespace.isEmpty)
            pln"@JSGlobal"
          else
            pln"""@JSGlobal("$currentJSNamespace${name.name}")"""
        }
        p"$sealedKw$abstractKw$kw $name"
        if (!sym.tparams.isEmpty)
          p"[${sym.tparams}]"

        {
          implicit val withSep = ListElemSeparator.WithKeyword
          pln"$constructorStr extends $parents {"
        }

        printMemberDecls(sym)
        pln"}"

      case sym: ModuleSymbol =>
        pln"";
        if (sym.isGlobal) {
          pln"@js.native"
          if (currentJSNamespace.isEmpty)
            pln"@JSGlobal"
          else
            pln"""@JSGlobal("$currentJSNamespace${name.name}")"""
          pln"object $name extends js.Object {"
        } else {
          pln"object $name {"
        }
        printMemberDecls(sym)
        pln"}"

      case sym: TypeAliasSymbol =>
        p"  type $name"
        if (!sym.tparams.isEmpty)
          p"[${sym.tparams}]"
        pln" = ${sym.alias}"

      case sym: FieldSymbol =>
        sym.jsName foreach { jsName =>
          pln"""  @JSName("$jsName")"""
        }
        val access =
          if (sym.modifiers(Modifier.Protected)) "protected "
          else ""
        val decl =
          if (sym.modifiers(Modifier.Const)) "val"
          else if (sym.modifiers(Modifier.ReadOnly)) "def"
          else "var"
        p"  $access$decl $name: ${sym.tpe}"
        if (!sym.modifiers(Modifier.Abstract))
          p" = js.native"
        pln""

      case sym: MethodSymbol =>
        val params = sym.params

        if (name == Name.CONSTRUCTOR) {
          if (!params.isEmpty)
            pln"  def this($params) = this()"
        } else {
          sym.jsName foreach { jsName =>
            pln"""  @JSName("$jsName")"""
          }
          if (sym.isBracketAccess)
            pln"""  @JSBracketAccess"""
          val modifiers =
            if (sym.needsOverride) "override " else ""
          p"  ${modifiers}def $name"
          if (!sym.tparams.isEmpty)
            p"[${sym.tparams}]"
          p"($params): ${sym.resultType}"
          if (!sym.modifiers(Modifier.Abstract))
            p" = js.native"
          pln""
        }

      case sym: ParamSymbol =>
        p"$name: ${sym.tpe}${if (sym.optional) " = ???" else ""}"

      case sym: TypeParamSymbol =>
        p"$name"
        sym.upperBound.foreach(bound => p" <: $bound")
    }
  }

  private def printMemberDecls(owner: ContainerSymbol) {
    val (constructors, others) =
      owner.members.toList.partition(_.name == Name.CONSTRUCTOR)
    for (sym <- constructors ++ others)
      printSymbol(sym)
  }

  private def canBeTopLevel(sym: Symbol): Boolean =
    sym.isInstanceOf[ContainerSymbol]

  private def isParameterlessConstructor(sym: Symbol): Boolean = {
    sym match {
      case sym: MethodSymbol =>
        sym.name == Name.CONSTRUCTOR && sym.params.isEmpty
      case _ =>
        false
    }
  }

  def printTypeRef(tpe: TypeRef) {
    tpe match {
      case TypeRef(typeName, Nil) =>
        p"$typeName"

      case TypeRef.Union(types) =>
        implicit val withPipe = ListElemSeparator.Pipe
        p"$types"

      case TypeRef.Intersection(types) =>
        implicit val withWith = ListElemSeparator.WithKeyword
        p"$types"

      case TypeRef.This =>
        p"this.type"

      case TypeRef.Singleton(termRef) =>
        p"$termRef.type"

      case TypeRef.Repeated(underlying) =>
        p"$underlying*"

      case TypeRef(typeName, targs) =>
        p"$typeName[$targs]"
    }
  }

  def printWildcard(wc: Wildcard): Unit = {
    wc match {
      case Wildcard(None) =>
        p"_"

      case Wildcard(Some(typeRefOrWildcard)) =>
        p"_ <: $typeRefOrWildcard"
    }
  }

  private def print(x: Any) {
    x match {
      case x: Symbol => printSymbol(x)
      case x: TypeRef => printTypeRef(x)
      case x: Wildcard => printWildcard(x)
      case QualifiedName(Name.scala, Name.scalajs, Name.js, name) =>
        output.print("js.")
        output.print(name)
      case QualifiedName(Name.scala, name) => output.print(name)
      case QualifiedName(Name.java, Name.lang, name) => output.print(name)
      case _ => output.print(x)
    }
  }
}

object Printer {
  private class ListElemSeparator(val s: String) extends AnyVal

  private object ListElemSeparator {
    val Comma = new ListElemSeparator(", ")
    val Pipe = new ListElemSeparator(" | ")
    val WithKeyword = new ListElemSeparator(" with ")
  }

  private implicit class OutputHelper(val sc: StringContext) extends AnyVal {
    def p(args: Any*)(implicit printer: Printer,
        sep: ListElemSeparator = ListElemSeparator.Comma) {
      val strings = sc.parts.iterator
      val expressions = args.iterator

      val output = printer.output
      output.print(strings.next())
      while (strings.hasNext) {
        expressions.next() match {
          case seq: Seq[_] =>
            val iter = seq.iterator
            if (iter.hasNext) {
              printer.print(iter.next())
              while (iter.hasNext) {
                output.print(sep.s)
                printer.print(iter.next())
              }
            }

          case expr =>
            printer.print(expr)
        }
        output.print(strings.next())
      }
    }

    def pln(args: Any*)(implicit printer: Printer,
        sep: ListElemSeparator = ListElemSeparator.Comma) {
      p(args:_*)
      printer.output.println()
    }
  }
}
