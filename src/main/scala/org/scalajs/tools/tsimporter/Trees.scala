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

  case class FunctionDecl(name: Ident, signature: FunSignature) extends DeclTree

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

  case class ClassDecl(name: TypeName, tparams: List[TypeParam],
      parent: Option[TypeRef], implements: List[TypeRef],
      membmers: List[MemberTree]) extends DeclTree

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
