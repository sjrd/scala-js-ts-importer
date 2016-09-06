/* TypeScript importer for Scala.js
 * Copyright 2013-2014 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package org.scalajs.tools.tsimporter.parser

import org.scalajs.tools.tsimporter.Trees._

import java.io.File

import scala.collection.mutable.ListBuffer

import scala.util.parsing.combinator._
import scala.util.parsing.combinator.token._
import scala.util.parsing.combinator.syntactical._
import scala.util.parsing.input._

class TSDefParser extends StdTokenParsers with ImplicitConversions {

  type Tokens = StdTokens
  val lexical: TSDefLexical = new TSDefLexical

  lexical.reserved ++= List(
      // Value keywords
      "true", "false",

      // Current JavaScript keywords
      "break", "case", "catch", "continue", "debugger", "default", "delete",
      "do", "else", "finally", "for", "function", "if", "in", "instanceof",
      "new", "return", "switch", "this", "throw", "try", "typeof", "var",
      "void", "while", "with",

      // Future reserved keywords - some used in TypeScript
      "class", "const", "enum", "export", "extends", "import", "super",

      // Future reserved keywords in Strict mode - some used in TypeScript
      "implements", "interface", "let", "package", "private", "protected",
      "public", "static", "yield",

      // Additional keywords of TypeScript
      "declare", "module", "type", "namespace"
  )

  lexical.delimiters ++= List(
      "{", "}", "(", ")", "[", "]", "<", ">",
      ".", ";", ",", "?", ":", "=", "|",
      // TypeScript-specific
      "...", "=>"
  )

  def parseDefinitions(input: Reader[Char]) =
    phrase(ambientDeclarations)(new lexical.Scanner(input))

  lazy val ambientDeclarations: Parser[List[DeclTree]] =
    rep(ambientDeclaration)

  lazy val ambientDeclaration: Parser[DeclTree] =
    opt("declare") ~> opt("export") ~> moduleElementDecl1

  lazy val ambientModuleDecl: Parser[DeclTree] =
    ("module" | "namespace") ~> rep1sep(propertyName, ".") ~ moduleBody ^^ {
      case nameParts ~ body =>
        nameParts.init.foldRight(ModuleDecl(nameParts.last, body)) {
          (name, inner) => ModuleDecl(name, inner :: Nil)
        }
    }

  lazy val moduleBody: Parser[List[DeclTree]] =
    "{" ~> rep(moduleElementDecl) <~ "}" ^^ (_.flatten)

  lazy val moduleElementDecl: Parser[Option[DeclTree]] = (
      "export" ~> (
          moduleElementDecl1 ^^ (Some(_))
        | "=" ~> identifier <~ ";" ^^^ None)
    | moduleElementDecl1 ^^ (Some(_))
  )

  lazy val moduleElementDecl1: Parser[DeclTree] = (
      ambientModuleDecl | ambientVarDecl | ambientFunctionDecl
    | ambientEnumDecl | ambientClassDecl | ambientInterfaceDecl
    | typeAliasDecl
  )

  lazy val ambientVarDecl: Parser[DeclTree] =
    "var" ~> identifier ~ optTypeAnnotation <~ opt(";") ^^ VarDecl

  lazy val ambientFunctionDecl: Parser[DeclTree] =
    "function" ~> identifier ~ functionSignature <~ opt(";") ^^ FunctionDecl

  lazy val ambientEnumDecl: Parser[DeclTree] =
    "enum" ~> typeName ~ ("{" ~> ambientEnumBody <~ "}") ^^ EnumDecl

  lazy val ambientEnumBody: Parser[List[Ident]] =
    repsep(identifier <~ opt("=" ~ numericLit), ",") <~ opt(",")

  lazy val ambientClassDecl: Parser[DeclTree] =
    "class" ~> typeName ~ tparams ~ classParent ~ classImplements ~ memberBlock <~ opt(";") ^^ ClassDecl

  lazy val ambientInterfaceDecl: Parser[DeclTree] =
    "interface" ~> typeName ~ tparams ~ intfInheritance ~ memberBlock <~ opt(";") ^^ InterfaceDecl

  lazy val typeAliasDecl: Parser[DeclTree] =
    "type" ~> typeName ~ tparams ~ ("=" ~> typeDesc) <~ opt(";") ^^ TypeAliasDecl

  lazy val tparams = (
      "<" ~> rep1sep(typeParam, ",") <~ ">"
    | success(Nil)
  )

  lazy val typeParam: Parser[TypeParam] =
    typeName ~ opt("extends" ~> typeRef) ^^ TypeParam

  lazy val classParent =
    opt("extends" ~> typeRef)

  lazy val classImplements = (
      "implements" ~> repsep(typeRef, ",")
    | success(Nil)
  )

  lazy val intfInheritance = (
      "extends" ~> repsep(typeRef, ",")
    | success(Nil)
  )

  lazy val functionSignature =
    tparams ~ ("(" ~> repsep(functionParam, ",") <~ ")") ~ optResultType ^^ FunSignature

  lazy val functionParam =
    repeatedParamMarker ~ identifier ~ optionalMarker ~ optParamType ^^ {
      case false ~ i ~ o ~ t =>
        FunParam(i, o, t)
      case _ ~ i ~ o ~ Some(ArrayType(t)) =>
        FunParam(i, o, Some(RepeatedType(t)))
      case _ ~ i ~ o ~ t =>
        Console.err.println(
            s"Warning: Dropping repeated marker of param $i because its type $t is not an array type")
        FunParam(i, o, t)
    }

  lazy val repeatedParamMarker =
    opt("...") ^^ (_.isDefined)

  lazy val optionalMarker =
    opt("?") ^^ (_.isDefined)

  lazy val optParamType =
    opt(":" ~> paramType)

  lazy val paramType: Parser[TypeTree] = (
      typeDesc
    | stringLiteral ^^ ConstantType
  )

  lazy val optResultType =
    opt(":" ~> resultType)

  lazy val resultType: Parser[TypeTree] = (
      ("void" ^^^ TypeRef(CoreType("void")))
    | typeDesc
  )

  lazy val optTypeAnnotation =
    opt(typeAnnotation)

  lazy val typeAnnotation =
    ":" ~> typeDesc

  lazy val typeDesc: Parser[TypeTree] =
    rep1sep(singleTypeDesc, "|") ^^ {
      _.reduceLeft(UnionType)
    }

  lazy val singleTypeDesc: Parser[TypeTree] =
    baseTypeDesc ~ rep("[" ~ "]") ^^ {
      case base ~ arrayDims =>
        (base /: arrayDims) {
          (elem, _) => ArrayType(elem)
        }
    }

  lazy val baseTypeDesc: Parser[TypeTree] = (
      typeRef
    | objectType
    | functionType
    | typeQuery
    | tupleType
    | "(" ~> typeDesc <~ ")"
  )

  lazy val typeRef: Parser[TypeRef] =
    baseTypeRef ~ opt(typeArgs) ^^ {
      case base ~ optTargs =>
        TypeRef(base, optTargs getOrElse Nil)
    }

  lazy val baseTypeRef: Parser[BaseTypeRef] =
    rep1sep("void" | ident, ".") ^^ { parts =>
      if (parts.tail.isEmpty) typeNameToTypeRef(parts.head)
      else QualifiedTypeName(parts.init map Ident, TypeName(parts.last))
    }

  lazy val typeArgs: Parser[List[TypeTree]] =
    "<" ~> rep1sep(typeDesc, ",") <~ ">"

  lazy val functionType: Parser[TypeTree] =
    tparams ~ ("(" ~> repsep(functionParam, ",") <~ ")") ~ ("=>" ~> resultType) ^^ {
      case tparams ~ params ~ resultType =>
        FunctionType(FunSignature(tparams, params, Some(resultType)))
    }

  lazy val typeQuery: Parser[TypeTree] =
    "typeof" ~> rep1sep(ident, ".") ^^ { parts =>
      TypeQuery(QualifiedIdent(parts.init.map(Ident), Ident(parts.last)))
    }

  lazy val tupleType: Parser[TypeTree] =
    "[" ~> rep1sep(typeDesc, ",") <~ "]" ^^ { parts =>
      TupleType(parts)
    }

  lazy val objectType: Parser[TypeTree] =
    memberBlock ^^ ObjectType

  lazy val memberBlock: Parser[List[MemberTree]] =
    "{" ~> rep(typeMember <~ opt(";" | ",")) <~ "}"

  lazy val typeMember: Parser[MemberTree] =
    callMember | constructorMember | indexMember | namedMember

  lazy val callMember: Parser[MemberTree] =
    functionSignature ^^ CallMember

  lazy val constructorMember: Parser[MemberTree] =
    "new" ~> functionSignature ^^ ConstructorMember

  lazy val indexMember: Parser[MemberTree] =
    ("[" ~> identifier ~ typeAnnotation <~ "]") ~ typeAnnotation ^^ IndexMember

  lazy val namedMember: Parser[MemberTree] =
    maybeStaticPropName ~ optionalMarker >> {
      case (name, static) ~ optional => (
          functionSignature ^^ (FunctionMember(name, optional, _, static))
        | typeAnnotation ^^ (PropertyMember(name, optional, _, static))
      )
    }

  lazy val maybeStaticPropName: Parser[(PropertyName, Boolean)] = (
      "static" ~> propertyName ^^ staticPropName
    | propertyName ^^ nonStaticPropName
  )

  val staticPropName = (p: PropertyName) => (p, true)
  val nonStaticPropName = (p: PropertyName) => (p, false)

  lazy val identifier =
    identifierName ^^ Ident

  lazy val typeName =
    identifierName ^^ TypeName

  lazy val identifierName = accept("IdentifierName", {
    case lexical.Identifier(chars)                                  => chars
    case lexical.Keyword(chars) if chars.forall(Character.isLetter) => chars
  })

  lazy val propertyName: Parser[PropertyName] =
    identifier | stringLiteral

  lazy val stringLiteral: Parser[StringLiteral] =
    stringLit ^^ StringLiteral

  private val isCoreTypeName =
    Set("any", "void", "number", "bool", "boolean", "string", "null", "undefined")

  def typeNameToTypeRef(name: String): BaseTypeRef =
    if (isCoreTypeName(name)) CoreType(name)
    else TypeName(name)

  object ArrayType {
    def apply(elem: TypeTree): TypeRef =
      TypeRef(TypeName("Array"), List(elem))

    def unapply(typeRef: TypeRef): Option[TypeTree] = typeRef match {
      case TypeRef(TypeName("Array"), List(elem)) => Some(elem)
      case _ => None
    }
  }
}
