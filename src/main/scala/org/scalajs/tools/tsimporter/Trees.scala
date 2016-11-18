/* TypeScript importer for Scala.js
 * Copyright 2013-2014 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package org.scalajs.tools.tsimporter

import scala.util.parsing.input.Positional

object Trees {
  // Tree

  abstract sealed class Tree extends Positional {
    /*override def toString() = {
      val baos = new java.io.ByteArrayOutputStream()
      val writer = new java.io.PrintWriter(baos)
      val printer = new TreePrinter(writer)
      printer.printTree(this)
      writer.close()
      baos.toString()
    }*/
  }

  sealed trait DeclTree extends Tree
  sealed trait TermTree extends Tree
  sealed trait TypeTree extends Tree
  sealed trait MemberTree extends Tree

  case class Block(stmts: List[TermTree]) extends TermTree

  case class BlockExpr(stmts: List[TermTree], expr: TermTree) extends TermTree

  case class VarDef(name: Ident, tpe: Option[TypeTree],
      value: Option[TermTree]) extends TermTree

  case class If(cond: TermTree, thenp: TermTree, elsep: TermTree) extends TermTree

  case class While(cond: TermTree, body: TermTree) extends TermTree

  case class DoWhile(body: TermTree, cond: TermTree) extends TermTree

  case class ForIn(varName: Ident, obj: TermTree, body: TermTree) extends TermTree

  case class ForOf(varName: Ident, iterable: TermTree, body: TermTree) extends TermTree

  case class Continue() extends TermTree

  case class Break() extends TermTree

  case class Return(expr: Option[TermTree]) extends TermTree

  case class Switch(scrutinee: TermTree, cases: List[(TermTree, TermTree)],
      defaultCase: Option[TermTree]) extends TermTree

  case class Throw(expr: TermTree) extends TermTree

  case class TryCatch(body: TermTree, exVar: Ident, handler: TermTree) extends TermTree

  case class TryFinally(body: TermTree, finalizer: TermTree) extends TermTree

  case class IfExpr(cond: TermTree, thenp: TermTree, elsep: TermTree) extends TermTree

  case class This() extends TermTree

  case class VarRef(ident: Ident) extends TermTree

  case class TemplateString(funName: Option[Ident], content: String) extends TermTree

  case class Super() extends TermTree

  case class ObjectConstr(fields: List[FieldDef]) extends TermTree

  sealed trait FieldDef extends Tree

  case class NamedField(name: TermTree, value: TermTree) extends FieldDef
  case class GetterDef(name: TermTree, body: TermTree) extends FieldDef
  case class SetterDef(name: TermTree, param: FunParam, body: TermTree) extends FieldDef
  // and FunctionDecl

  case class ArrayConstr(elems: List[TermTree]) extends TermTree

  case class Spread(array: TermTree) extends TermTree

  case class FunExpr(signature: FunSignature, body: TermTree, arrow: Boolean) extends TermTree

  case class DotSelect(qual: TermTree, item: Ident) extends TermTree

  case class BracketSelect(qual: TermTree, item: TermTree) extends TermTree

  case class New(ctor: TermTree, targs: List[TypeRef],
      args: List[TermTree]) extends TermTree

  case class FunCall(fun: TermTree, targs: List[TypeRef],
      args: List[TermTree]) extends TermTree

  case class Cast(expr: TermTree, tpe: TypeRef) extends TermTree

  case class UnaryOp(op: UnaryOp.Code, arg: TermTree) extends TermTree

  object UnaryOp {
    /** Codes are the same as in the IR. */
    type Code = Int

    final val + = 1
    final val - = 2
    final val ~ = 3
    final val ! = 4

    final val Pre_++ = 5
    final val Pre_-- = 6
    final val Post_++ = 7
    final val Post_-- = 8

    final val typeof = 9

    final val void = 10
  }

  case class BinaryOp(op: BinaryOp.Code, lhs: TermTree, rhs: TermTree) extends TermTree

  object BinaryOp {
    type Code = Int

    final val === = 1
    final val !== = 2

    final val + = 3
    final val - = 4
    final val * = 5
    final val / = 6
    final val % = 7

    final val |   = 8
    final val &   = 9
    final val ^   = 10
    final val <<  = 11
    final val >>  = 12
    final val >>> = 13

    final val <  = 14
    final val <= = 15
    final val >  = 16
    final val >= = 17

    final val && = 18
    final val || = 19

    final val in         = 20
    final val instanceof = 21

    final val Buggy_== = 22
    final val Buggy_!= = 23
  }

  /** `+=` and the like. */
  case class OpAssign(op: BinaryOp.Code, lhs: TermTree, rhs: TermTree) extends TermTree

  case class Delete(qual: TermTree, item: TermTree) extends TermTree



  // Identifiers and properties

  sealed trait PropertyName extends TermTree {
    def name: String
  }

  object PropertyName {
    def apply(name: String): PropertyName = {
      if (Ident.isValidIdentifier(name)) Ident(name)
      else StringLiteral(name)
    }

    def unapply(tree: PropertyName): Some[String] =
      Some(tree.name)
  }

  case class Ident(name: String) extends Tree with PropertyName {
    Ident.requireValidIdent(name)
  }

  object Ident extends (String => Ident) {
    final def isValidIdentifier(name: String): Boolean = {
      val c = name.head
      (c == '$' || c == '_' || c.isUnicodeIdentifierStart) &&
          name.tail.forall(c => c == '$' || c.isUnicodeIdentifierPart)
    }

    @inline final def requireValidIdent(name: String) {
      require(isValidIdentifier(name), s"${name} is not a valid identifier")
    }
  }

  case class QualifiedIdent(qualifier: List[Ident], name: Ident) extends Tree

  // Declarations

  case class ModuleDecl(name: PropertyName, members: List[DeclTree]) extends DeclTree

  case class VarDecl(name: Ident, tpe: Option[TypeTree]) extends DeclTree

  case class FunctionDecl(name: Ident, signature: FunSignature, body: TermTree)
      extends DeclTree with FieldDef

  // Function signature

  case class FunSignature(tparams: List[TypeParam], params: List[FunParam],
      resultType: Option[TypeTree]) extends Tree

  case class FunParam(name: Ident, optional: Boolean, tpe: Option[TypeTree]) extends Tree

  // Type parameters

  case class TypeParam(name: TypeName, upperBound: Option[TypeRef]) extends Tree

  // Literals

  sealed trait Literal extends TermTree

  case class Undefined() extends Literal

  case class Null() extends Literal

  case class BooleanLiteral(value: Boolean) extends Literal

  case class NumberLiteral(value: Double) extends Literal

  case class StringLiteral(value: String) extends Literal with PropertyName {
    override def name = value
  }

  // Type descriptions

  case class TypeDecl(name: TypeName, tpe: TypeTree) extends DeclTree

  case class EnumDecl(name: TypeName, members: List[Ident]) extends DeclTree

  case class ClassDecl(name: Option[TypeName], tparams: List[TypeParam],
      parent: Option[TypeRef], implements: List[TypeRef],
      membmers: List[MemberTree]) extends DeclTree with TermTree

  case class InterfaceDecl(name: TypeName, tparams: List[TypeParam],
      inheritance: List[TypeRef], members: List[MemberTree]) extends DeclTree

  case class TypeAliasDecl(name: TypeName, tparams: List[TypeParam],
      alias: TypeTree) extends DeclTree

  case class TypeRef(name: BaseTypeRef, tparams: List[TypeTree] = Nil) extends TypeTree

  sealed abstract class BaseTypeRef extends Tree

  case class CoreType(name: String) extends BaseTypeRef

  case class TypeName(name: String) extends BaseTypeRef {
    Ident.requireValidIdent(name)
  }

  case class QualifiedTypeName(qualifier: List[Ident], name: TypeName) extends BaseTypeRef

  case class ConstantType(literal: Literal) extends TypeTree

  case class ObjectType(members: List[MemberTree]) extends TypeTree

  case class FunctionType(signature: FunSignature) extends TypeTree

  case class UnionType(left: TypeTree, right: TypeTree) extends TypeTree

  case class TupleType(tparams: List[TypeTree]) extends TypeTree

  case class TypeQuery(expr: QualifiedIdent) extends TypeTree

  case class RepeatedType(underlying: TypeTree) extends TypeTree

  // Type members

  case class CallMember(signature: FunSignature) extends MemberTree

  case class ConstructorMember(signature: FunSignature) extends MemberTree

  case class IndexMember(indexName: Ident, indexType: TypeTree, valueType: TypeTree) extends MemberTree

  case class PropertyMember(name: PropertyName, optional: Boolean,
      tpe: TypeTree, static: Boolean) extends MemberTree

  case class FunctionMember(name: PropertyName, optional: Boolean,
      signature: FunSignature, static: Boolean) extends MemberTree
}
