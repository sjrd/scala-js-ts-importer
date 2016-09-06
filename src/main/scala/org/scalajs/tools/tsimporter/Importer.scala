/* TypeScript importer for Scala.js
 * Copyright 2013-2014 LAMP/EPFL
 * @author  Sébastien Doeraene
 */

package org.scalajs.tools.tsimporter

import Trees.{ TypeRef => TypeRefTree, _ }
import sc._

/** The meat and potatoes: the importer
 *  It reads the TypeScript AST and produces (hopefully) equivalent Scala
 *  code.
 */
class Importer(val output: java.io.PrintWriter) {
  import Importer._

  /** Entry point */
  def apply(declarations: List[DeclTree], outputPackage: String) {
    val rootPackage = new PackageSymbol(Name.EMPTY)

    for (declaration <- declarations)
      processDecl(rootPackage, declaration)

    new Printer(output, outputPackage).printSymbol(rootPackage)
  }

  private def processDecl(owner: ContainerSymbol, declaration: DeclTree) {
    declaration match {
      case ModuleDecl(PropertyNameName(name), innerDecls) =>
        assert(owner.isInstanceOf[PackageSymbol],
            s"Found package $name in non-package $owner")
        val sym = owner.asInstanceOf[PackageSymbol].getPackageOrCreate(name)

        for (innerDecl <- innerDecls)
          processDecl(sym, innerDecl)

      case VarDecl(IdentName(name), Some(tpe @ ObjectType(members))) =>
        val sym = owner.getModuleOrCreate(name)
        processMembersDecls(owner, sym, members)

      case TypeDecl(TypeNameName(name), tpe @ ObjectType(members)) =>
        val sym = owner.getClassOrCreate(name)
        processMembersDecls(owner, sym, members)

      case EnumDecl(TypeNameName(name), members) =>
        // Type
        val tsym = owner.getClassOrCreate(name)
        tsym.isSealed = true

        // Module
        val sym = owner.getModuleOrCreate(name)
        for (IdentName(name) <- members) {
          val m = sym.newField(name)
          m.protectName()
          m.tpe = TypeRef(tsym.name)
        }
        val applySym = sym.newMethod(Name("apply"))
        applySym.params += new ParamSymbol(Name("value"), TypeRef(tsym.name))
        applySym.resultType = TypeRef.String
        applySym.isBracketAccess = true

      case ClassDecl(TypeNameName(name), tparams, parent, implements, members) =>
        val sym = owner.getClassOrCreate(name)
        sym.isTrait = false
        parent.foreach(sym.parents += typeToScala(_))
        for {
          parent <- implements.map(typeToScala)
          if !sym.parents.contains(parent)
        } {
          sym.parents += parent
        }
        sym.tparams ++= typeParamsToScala(tparams)
        processMembersDecls(owner, sym, members)
        if (!sym.members.exists(_.name == Name.CONSTRUCTOR)) {
          processDefDecl(sym, Name.CONSTRUCTOR,
              FunSignature(Nil, Nil, Some(TypeRefTree(CoreType("void")))))
        }

      case InterfaceDecl(TypeNameName(name), tparams, inheritance, members) =>
        val sym = owner.getClassOrCreate(name)
        for {
          parent <- inheritance.map(typeToScala)
          if !sym.parents.contains(parent)
        } {
          sym.parents += parent
        }
        sym.tparams ++= typeParamsToScala(tparams)
        processMembersDecls(owner, sym, members)

      case TypeAliasDecl(TypeNameName(name), tparams, alias) =>
        val sym = owner.newTypeAlias(name)
        sym.tparams ++= typeParamsToScala(tparams)
        sym.alias = typeToScala(alias)

      case VarDecl(IdentName(name), TypeOrAny(tpe)) =>
        val sym = owner.newField(name)
        sym.tpe = typeToScala(tpe)

      case FunctionDecl(IdentName(name), signature) =>
        processDefDecl(owner, name, signature)

      case _ =>
        owner.members += new CommentSymbol("??? "+declaration)
    }
  }

  private def processMembersDecls(enclosing: ContainerSymbol,
      owner: ContainerSymbol, members: List[MemberTree]) {

    val OwnerName = owner.name

    lazy val companionClassRef = {
      val tparams = enclosing.findClass(OwnerName) match {
        case Some(clazz) =>
          clazz.tparams.toList.map(tp => TypeRefTree(TypeNameName(tp.name), Nil))
        case _ => Nil
      }
      TypeRefTree(TypeNameName(OwnerName), tparams)
    }

    for (member <- members) member match {
      case CallMember(signature) =>
        processDefDecl(owner, Name("apply"), signature, protectName = false)

      case ConstructorMember(sig @ FunSignature(tparamsIgnored, params, Some(resultType)))
      if owner.isInstanceOf[ModuleSymbol] && resultType == companionClassRef =>
        val classSym = enclosing.getClassOrCreate(owner.name)
        classSym.isTrait = false
        processDefDecl(classSym, Name.CONSTRUCTOR,
            FunSignature(Nil, params, Some(TypeRefTree(CoreType("void")))))

      case PropertyMember(PropertyNameName(name), opt, tpe, true) =>
        assert(owner.isInstanceOf[ClassSymbol],
            s"Cannot process static member $name in module definition")
        val module = enclosing.getModuleOrCreate(owner.name)
        processPropertyDecl(module, name, tpe)

      case PropertyMember(PropertyNameName(name), opt, tpe, _) =>
        processPropertyDecl(owner, name, tpe)

      case FunctionMember(PropertyName("constructor"), _, signature, false)
      if owner.isInstanceOf[ClassSymbol] =>
        owner.asInstanceOf[ClassSymbol].isTrait = false
        processDefDecl(owner, Name.CONSTRUCTOR,
            FunSignature(Nil, signature.params, Some(TypeRefTree(CoreType("void")))))

      case FunctionMember(PropertyNameName(name), opt, signature, true) =>
        assert(owner.isInstanceOf[ClassSymbol],
            s"Cannot process static member $name in module definition")
        val module = enclosing.getModuleOrCreate(owner.name)
        processDefDecl(module, name, signature)

      case FunctionMember(PropertyNameName(name), opt, signature, _) =>
        processDefDecl(owner, name, signature)

      case IndexMember(IdentName(indexName), indexType, valueType) =>
        val indexTpe = typeToScala(indexType)
        val valueTpe = typeToScala(valueType)

        val getterSym = owner.newMethod(Name("apply"))
        getterSym.params += new ParamSymbol(indexName, indexTpe)
        getterSym.resultType = valueTpe
        getterSym.isBracketAccess = true

        val setterSym = owner.newMethod(Name("update"))
        setterSym.params += new ParamSymbol(indexName, indexTpe)
        setterSym.params += new ParamSymbol(Name("v"), valueTpe)
        setterSym.resultType = TypeRef.Unit
        setterSym.isBracketAccess = true

      case _ =>
        owner.members += new CommentSymbol("??? "+member)
    }
  }

  private def processPropertyDecl(owner: ContainerSymbol, name: Name,
      tpe: TypeTree, protectName: Boolean = true) {
    if (name.name != "prototype") {
      tpe match {
        case ObjectType(members) if members.forall(_.isInstanceOf[CallMember]) =>
          // alternative notation for overload methods - #3
          for (CallMember(signature) <- members)
            processDefDecl(owner, name, signature, protectName)
        case _ =>
          val sym = owner.newField(name)
          if (protectName)
            sym.protectName()
          sym.tpe = typeToScala(tpe)
      }
    }
  }

  private def processDefDecl(owner: ContainerSymbol, name: Name,
      signature: FunSignature, protectName: Boolean = true) {
    // Discard specialized signatures
    if (signature.params.exists(_.tpe.exists(_.isInstanceOf[ConstantType])))
      return

    val sym = owner.newMethod(name)
    if (protectName)
      sym.protectName()

    sym.tparams ++= typeParamsToScala(signature.tparams)

    for (FunParam(IdentName(paramName), opt, TypeOrAny(tpe)) <- signature.params) {
      val paramSym = new ParamSymbol(paramName)
      paramSym.optional = opt
      tpe match {
        case RepeatedType(tpe0) =>
          paramSym.tpe = TypeRef.Repeated(typeToScala(tpe0))
        case _ =>
          paramSym.tpe = typeToScala(tpe)
      }
      sym.params += paramSym
    }

    sym.resultType = typeToScala(signature.resultType.orDynamic, true)

    owner.removeIfDuplicate(sym)
  }

  private def typeParamsToScala(tparams: List[TypeParam]): List[TypeParamSymbol] = {
    for (TypeParam(TypeNameName(tparam), upperBound) <- tparams) yield
      new TypeParamSymbol(tparam, upperBound map typeToScala)
  }

  private def typeToScala(tpe: TypeTree): TypeRef =
    typeToScala(tpe, false)

  private def typeToScala(tpe: TypeTree, anyAsDynamic: Boolean): TypeRef = {
    tpe match {
      case TypeRefTree(tpe: CoreType, Nil) =>
        coreTypeToScala(tpe, anyAsDynamic)

      case TypeRefTree(base, targs) =>
        val baseTypeRef = base match {
          case TypeName("Array") => QualifiedName.Array
          case TypeName("Function") => QualifiedName.FunctionBase
          case TypeNameName(name) => QualifiedName(name)
          case QualifiedTypeName(qualifier, TypeNameName(name)) =>
            val qual1 = qualifier map (x => Name(x.name))
            QualifiedName((qual1 :+ name): _*)
          case _: CoreType => throw new MatchError(base)
        }
        TypeRef(baseTypeRef, targs map typeToScala)

      case ObjectType(List(IndexMember(_, TypeRefTree(CoreType("string"), _), valueType))) =>
        val valueTpe = typeToScala(valueType)
        TypeRef(QualifiedName.Dictionary, List(valueTpe))

      case ObjectType(members) =>
        // ???
        TypeRef.Any

      case FunctionType(FunSignature(tparams, params, Some(resultType))) =>
        if (!tparams.isEmpty) {
          // Type parameters in function types are not supported
          TypeRef.Function
        } else if (params.exists(_.tpe.exists(_.isInstanceOf[RepeatedType]))) {
          // Repeated params in function types are not supported
          TypeRef.Function
        } else {
          val paramTypes =
            for (FunParam(_, _, TypeOrAny(tpe)) <- params)
              yield typeToScala(tpe)
          val resType = resultType match {
            case TypeRefTree(CoreType("any"), Nil) => TypeRef.ScalaAny
            case _ => typeToScala(resultType)
          }
          val targs = paramTypes :+ resType

          TypeRef(QualifiedName.Function(params.size), targs)
        }

      case UnionType(left, right) =>
        TypeRef.Union(typeToScala(left), typeToScala(right))

      case TypeQuery(expr) =>
        TypeRef.Singleton(QualifiedName((expr.qualifier :+ expr.name).map(
            ident => Name(ident.name)): _*))

      case TupleType(targs) =>
          TypeRef(QualifiedName.Tuple(targs.length), targs map typeToScala)

      case RepeatedType(underlying) =>
        TypeRef(Name.REPEATED, List(typeToScala(underlying)))

      case _ =>
        // ???
        TypeRef.Any
    }
  }

  private def coreTypeToScala(tpe: CoreType,
      anyAsDynamic: Boolean = false): TypeRef = {

    tpe.name match {
      case "any"       => if (anyAsDynamic) TypeRef.Dynamic else TypeRef.Any
      case "dynamic"   => TypeRef.Dynamic
      case "void"      => TypeRef.Unit
      case "number"    => TypeRef.Double
      case "bool"      => TypeRef.Boolean
      case "boolean"   => TypeRef.Boolean
      case "string"    => TypeRef.String
      case "null"      => TypeRef.Null
      case "undefined" => TypeRef.Unit
    }
  }
}

object Importer {
  private val AnyType = TypeRefTree(CoreType("any"))
  private val DynamicType = TypeRefTree(CoreType("dynamic"))

  private implicit class OptType(val optType: Option[TypeTree]) extends AnyVal {
    @inline def orAny: TypeTree = optType.getOrElse(AnyType)
    @inline def orDynamic: TypeTree = optType.getOrElse(DynamicType)
  }

  private object TypeOrAny {
    @inline def unapply(optType: Option[TypeTree]) = Some(optType.orAny)
  }

  private object IdentName {
    @inline def unapply(ident: Ident) =
      Some(Name(ident.name))
  }

  private object TypeNameName {
    @inline def apply(typeName: Name) =
      TypeName(typeName.name)
    @inline def unapply(typeName: TypeName) =
      Some(Name(typeName.name))
  }

  private object PropertyNameName {
    @inline def unapply(propName: PropertyName) =
      Some(Name(propName.name))
  }
}
