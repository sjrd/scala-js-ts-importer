/* TypeScript importer for Scala.js
 * Copyright 2013 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package scala.tools.scalajs.tsimporter.sc

import java.io.PrintWriter

class Printer(private val output: PrintWriter) {
  import Printer._

  private implicit val self = this

  def printSymbol(sym: Symbol) {
    val name = sym.name
    sym match {
      case comment: CommentSymbol =>
        pln"/* ${comment.text} */"

      case sym: PackageSymbol =>
        val (topLevels, packageObjectMembers) =
          sym.members.partition(canBeTopLevel)

        val parentPackage = name.init
        val thisPackage = name.last

        pln"package $name {"
        pln"";
        for (sym <- topLevels)
          printSymbol(sym)
        pln""

        if (!parentPackage.isRoot)
          pln"package $parentPackage {"
        pln"package object $thisPackage extends js.GlobalScope {"
        for (sym <- packageObjectMembers)
          printSymbol(sym)
        pln"}"
        if (!parentPackage.isRoot)
          pln"}"

      case sym: ClassSymbol =>
        val kw = if (sym.isTrait) "trait" else "class"
        val tparamsStr =
          if (sym.tparams.isEmpty) ""
          else sym.tparams.mkString("[", ", ", "]")
        val constructorStr =
          if (sym.isTrait) ""
          else if (sym.members.exists(isParameterlessConstructor)) ""
          else " protected ()"
        val parents =
          if (sym.parents.isEmpty) List(TypeRef.Object)
          else sym.parents.toList

        implicit val withSep = ListElemSeparator.WithKeyword
        pln"";
        pln"$kw $name$tparamsStr$constructorStr extends $parents {"
        printMemberDecls(sym)
        pln"}"

      case sym: ModuleSymbol =>
        pln"";
        pln"object $name extends js.Object {"
        printMemberDecls(sym)
        pln"}"

      case sym: FieldSymbol =>
        pln"  var $name: ${sym.tpe} = _"

      case sym: MethodSymbol =>
        val params = sym.params

        if (name.last == Name.CONSTRUCTOR) {
          if (!params.isEmpty)
            pln"  def this($params) = this()"
        } else {
          val tparamsStr =
            if (sym.tparams.isEmpty) ""
            else sym.tparams.mkString("[", ", ", "]")
          pln"  def $name$tparamsStr($params): ${sym.resultType} = ???"
        }

      case sym: ParamSymbol =>
        p"$name: ${sym.tpe}"
    }
  }

  private def printMemberDecls(owner: ContainerSymbol) {
    val (constructors, others) =
      owner.members.toList.partition(_.name.last == Name.CONSTRUCTOR)
    for (sym <- constructors ++ others)
      printSymbol(sym)
  }

  private def canBeTopLevel(sym: Symbol): Boolean =
    sym.isInstanceOf[ContainerSymbol]

  private def isParameterlessConstructor(sym: Symbol): Boolean = {
    sym match {
      case sym: MethodSymbol =>
        sym.name.last == Name.CONSTRUCTOR && sym.params.isEmpty
      case _ =>
        false
    }
  }

  def printTypeRef(tpe: TypeRef) {
    tpe match {
      case TypeRef(typeName, Nil) =>
        p"$typeName"

      case TypeRef.Repeated(underlying) =>
        p"$underlying*"

      case TypeRef(typeName, targs) =>
        p"$typeName[$targs]"
    }
  }

  private def print(x: Any) {
    x match {
      case x: Symbol => printSymbol(x)
      case x: TypeRef => printTypeRef(x)
      case QualifiedName(Name.scala, Name.js, name) =>
        output.print("js.")
        output.print(name)
      case QualifiedName(Name.scala, name) => output.print(name)
      case _ => output.print(x)
    }
  }
}

object Printer {
  private class ListElemSeparator(val s: String) extends AnyVal

  private object ListElemSeparator {
    val Comma = new ListElemSeparator(", ")
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
