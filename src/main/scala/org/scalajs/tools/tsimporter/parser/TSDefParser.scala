/* TypeScript importer for Scala.js
 * Copyright 2013-2014 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package org.scalajs.tools.tsimporter.parser

import org.scalajs.tools.tsimporter.Trees._

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
      "readonly",

      // Future reserved keywords in Strict mode - some used in TypeScript
      "implements", "interface", "let", "package", "private", "protected",
      "public", "static", "yield",

      // Additional keywords of TypeScript
      "declare", "module", "type", "namespace", "keyof"
  )

  lexical.delimiters ++= List(
      "{", "}", "(", ")", "[", "]", "<", ">",
      ".", ";", ",", "?", ":", "=", "|", "&", "*",
      // TypeScript-specific
      "...", "=>"
  )

  def parseDefinitions(input: Reader[Char]) =
    phrase(ambientDeclarations)(new lexical.Scanner(input))

  lazy val ambientDeclarations: Parser[List[DeclTree]] =
    rep(ambientDeclaration).map(_.flatMap(_.toList))

  lazy val ambientDeclaration: Parser[Option[DeclTree]] = ((
      opt("declare") ~> opt("export") ~> moduleElementDecl1
    | opt("export") ~> opt("declare") ~> moduleElementDecl1
  ).map(Some(_))
    | "export" ~> lexical.Identifier("as") ~> "namespace" ~> identifier <~ opt(";") ^^^ None
    | "export" ~> "default" ~> identifier <~ opt(";") ^^^ None
  )

  lazy val ambientModuleDecl: Parser[DeclTree] =
    ("module" | "namespace") ~> rep1sep(propertyName, ".") ~ moduleBody ^^ {
      case nameParts ~ body =>
        nameParts.init.foldRight(ModuleDecl(nameParts.last, body)) {
          (name, inner) => ModuleDecl(name, inner :: Nil)
        }
    }

  lazy val moduleBody: Parser[List[DeclTree]] =
    "{" ~> rep(moduleElementDecl) <~ "}" ^^ (_.flatten)

  lazy val topLevelExportDecl: Parser[DeclTree] =
    "=" ~> identifier <~ ";" ^^ TopLevelExportDecl

  lazy val moduleElementDecl: Parser[Option[DeclTree]] = (
      "export" ~> (
          moduleElementDecl1 ^^ (Some(_))
        | "=" ~> identifier <~ ";" ^^^ None)
    | moduleElementDecl1 ^^ (Some(_))
  )

  lazy val moduleElementDecl1: Parser[DeclTree] = (
      ambientModuleDecl | ambientVarDecl | ambientFunctionDecl
    | ambientEnumDecl | ambientClassDecl | ambientInterfaceDecl
    | ambientConstDecl | ambientLetDecl | typeAliasDecl
    | importDecl
    | topLevelExportDecl
  )

  lazy val ambientVarDecl: Parser[DeclTree] =
    "var" ~> identifier ~ optTypeAnnotation <~ opt(";") ^^ VarDecl

  lazy val ambientLetDecl: Parser[DeclTree] =
    "let" ~> identifier ~ optTypeAnnotation <~ opt(";") ^^ LetDecl

  lazy val ambientConstDecl: Parser[DeclTree] =
    "const" ~> identifier ~ optTypeAnnotation <~ opt(";") ^^ ConstDecl

  lazy val ambientFunctionDecl: Parser[DeclTree] =
    "function" ~> identifier ~ functionSignature <~ opt(";") ^^ FunctionDecl

  lazy val ambientEnumDecl: Parser[DeclTree] =
    "enum" ~> typeName ~ ("{" ~> ambientEnumBody <~ "}") ^^ EnumDecl

  lazy val ambientEnumBody: Parser[List[Ident]] =
    repsep(identifier <~ opt("=" ~ (numericLit | stringLit) ), ",") <~ opt(",")

  lazy val ambientClassDecl: Parser[DeclTree] =
    (abstractModifier <~ "class") ~ typeName ~ tparams ~ classParent ~ classImplements ~ memberBlock <~ opt(";") ^^ {
      case am ~ tn ~ tp ~ cp ~ ci ~ mb => ClassDecl(tn, tp, cp, ci, mb, am)
    }

  lazy val ambientInterfaceDecl: Parser[DeclTree] =
    "interface" ~> typeName ~ tparams ~ intfInheritance ~ memberBlock <~ opt(";") ^^ InterfaceDecl

  lazy val typeAliasDecl: Parser[DeclTree] =
    "type" ~> typeName ~ tparams ~ ("=" ~> typeDesc) <~ opt(";") ^^ TypeAliasDecl

  lazy val importDecl: Parser[DeclTree] =
    "import" ~> opt(
      (
          identifier
        |  "{" ~ importIdentifierSeq ~ "}"
        | "*" ~ lexical.Identifier("as") ~ identifier
      ) ~ lexical.Identifier("from")
    ) ~ stringLiteral <~ ";" ^^^ ImportDecl

  lazy val importIdentifierSeq =
    rep1sep(identifier ~ opt(lexical.Identifier("as") ~ identifier), ",")

  lazy val abstractModifier =
    opt(lexical.Identifier("abstract")) ^^ (_.isDefined)

  lazy val tparams = (
      "<" ~> rep1sep(typeParam, ",") <~ ">"
    | success(Nil)
  )

  lazy val typeParam: Parser[TypeParam] =
    typeName ~ opt("extends" ~> typeDesc) <~ opt("=" ~> typeDesc) ^^ TypeParam

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
    tparams ~ ("(" ~> repsep(functionParam, ",") <~ opt(",") <~ ")") ~ optResultType ^^ FunSignature

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
    | numberLiteral ^^ ConstantType
    | booleanLiteral ^^ ConstantType
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
    unionTypeDesc

  lazy val unionTypeDesc: Parser[TypeTree] =
    opt("|") ~> rep1sep(intersectionTypeDesc, "|") ^^ {
      _.reduceLeft(UnionType)
    }

  lazy val intersectionTypeDesc: Parser[TypeTree] =
    rep1sep(singleTypeDesc, "&") ^^ {
      _.reduceLeft(IntersectionType)
    }

  lazy val singleTypeDesc: Parser[TypeTree] =
    baseTypeDesc ~ rep("[" ~> opt(typeDesc) <~ "]") ^^ {
      case base ~ arrayDims =>
        (base /: arrayDims) {
          case (elem, None) => ArrayType(elem)
          case (elem, Some(index)) => IndexedAccessType(elem, index)
        }
    }

  lazy val baseTypeDesc: Parser[TypeTree] = (
      typeRef
    | objectType
    | functionType
    | stringType
    | numberType
    | booleanType
    | typeQuery
    | tupleType
    | thisType
    | indexTypeQuery
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

  lazy val stringType: Parser[TypeTree] =
    stringLiteral ^^ ConstantType

  lazy val numberType: Parser[TypeTree] =
    numberLiteral ^^ ConstantType

  lazy val booleanType: Parser[TypeTree] =
    booleanLiteral ^^ ConstantType

  lazy val thisType: Parser[TypeTree] =
    "this" ^^^ PolymorphicThisType

  lazy val indexTypeQuery: Parser[TypeTree] =
    "keyof" ~> typeDesc ^^ IndexedQueryType

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
    callMember | constructorMember | indexMember | namedMember | privateMember

  lazy val callMember: Parser[MemberTree] =
    functionSignature ^^ CallMember

  lazy val constructorMember: Parser[MemberTree] =
    "new" ~> functionSignature ^^ ConstructorMember

  lazy val indexMember: Parser[MemberTree] =
    modifiers ~ ("[" ~> identifier ~ typeAnnotation <~ "]") ~ typeAnnotation ^^ {
      case mods ~ (indexName ~ indexType) ~ valueType =>
        IndexMember(indexName, indexType, valueType, mods)
    }

  lazy val namedMember: Parser[MemberTree] =
    modifiers ~ propertyName ~ optionalMarker >> {
      case mods ~ name ~ optional => (
          functionSignature ^^ (FunctionMember(name, optional, _, mods))
        | typeAnnotation ^^ (PropertyMember(name, optional, _, mods))
      )
    }

  lazy val privateMember =
    "private" ~> opt("static") ~> propertyName ~ opt(functionSignature | typeAnnotation) ^^^ PrivateMember

  lazy val modifiers: Parser[Modifiers] =
    rep(modifier).map(_.toSet)

  lazy val modifier: Parser[Modifier] = (
      "static" ^^^ Modifier.Static
    | "public" ^^^ Modifier.Public
    | "readonly" ^^^ Modifier.ReadOnly
    | "protected" ^^^ Modifier.Protected
    | lexical.Identifier("abstract") ^^^ Modifier.Abstract
  )

  lazy val identifier =
    identifierName ^^ Ident

  lazy val typeName =
    identifierName ^^ TypeName

  lazy val identifierName = accept("IdentifierName", {
    case lexical.Identifier(chars)                                  => chars
    case lexical.Keyword(chars) if chars.forall(Character.isLetter) => chars
  })

  lazy val propertyName: Parser[PropertyName] =
    identifier | stringLiteral | numberLiteral

  lazy val stringLiteral: Parser[StringLiteral] =
    stringLit ^^ StringLiteral

  lazy val numberLiteral: Parser[NumberLiteral] =
    numericLit ^^ { s =>
      val d = s.toDouble
      if (!s.contains(".") && d.isValidInt) {
        IntLiteral(d.toInt)
      } else {
        DoubleLiteral(d)
      }
    }

  lazy val booleanLiteral: Parser[BooleanLiteral] = (
      "true" ^^^ BooleanLiteral(true)
    | "false" ^^^ BooleanLiteral(false)
  )

  private val isCoreTypeName =
    Set("any", "void", "number", "bool", "boolean", "string", "null", "undefined", "never")

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
