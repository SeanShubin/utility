package com.seanshubin.utility.reflection

import scala.reflect.runtime._

class ReflectionImpl(simpleTypeConversions: Map[universe.Type, SimpleTypeConversion]) extends Reflection {
  private val mirror: universe.Mirror = universe.runtimeMirror(getClass.getClassLoader)

  override def pieceTogether[T: universe.TypeTag](dynamicValue: Any, staticClass: Class[T]): T = {
    val tpe: universe.Type = universe.typeOf[T]
    val result = pieceTogetherAny(dynamicValue, tpe).asInstanceOf[T]
    result
  }

  override def pullApart[T: universe.TypeTag](staticValue: T): Any = {
    val tpe = universe.typeTag[T].tpe
    val dynamicValue = pullApartAny(staticValue, tpe)
    dynamicValue
  }

  private def pieceTogetherAny(dynamicValue: Any, tpe: universe.Type): Any = {
    val result = simpleTypeConversions.get(tpe) match {
      case Some(simpleTypeConversion) => simpleTypeConversion.toStatic(dynamicValue.asInstanceOf[String])
      case None =>
        dynamicValue match {
          case map: Map[String, Any] => pieceTogetherObject(map, tpe)
          case seq: Seq[Any] => pieceTogetherSeq(seq, tpe)
        }
    }
    result
  }

  private def pieceTogetherObject(valueMap: Map[String, Any], tpe: universe.Type): Any = {
    val constructor: universe.MethodSymbol = tpe.decl(universe.termNames.CONSTRUCTOR).asMethod
    val constructorParameters: Seq[universe.TermSymbol] = constructor.typeSignature.paramLists.head.map(_.asTerm)
    val parameterList: Seq[Any] = createParameterList(constructorParameters, valueMap)
    val typeClass: universe.ClassSymbol = tpe.typeSymbol.asClass
    val classMirror: universe.ClassMirror = mirror.reflectClass(typeClass)
    val constructorMethod: universe.MethodMirror = classMirror.reflectConstructor(constructor)
    val constructed: Any = constructorMethod(parameterList: _*)
    constructed
  }

  private def pieceTogetherSeq(dynamicSeq: Seq[Any], tpe: universe.Type): Any = {
    val elementType: universe.Type = tpe.typeArgs.head
    def pieceTogetherElement(dynamicValue: Any): Any = {
      pieceTogetherAny(dynamicValue, elementType)
    }
    val staticSeq = dynamicSeq.map(pieceTogetherElement)
    staticSeq
  }

  private def createParameterList(constructorParameters: Seq[universe.TermSymbol], valueMap: Map[String, Any]): Seq[Any] = {
    def lookupValue(term: universe.TermSymbol): Any = {
      val parameterName = symbolName(term)
      val dynamicParameterValue = valueMap(parameterName)
      val parameterType: universe.Type = term.info
      val parameterValue = pieceTogetherAny(dynamicParameterValue, parameterType)
      parameterValue
    }
    val parameterList = constructorParameters.map(lookupValue)
    parameterList
  }

  private def symbolName(parameter: universe.Symbol): String = parameter.name.decodedName.toString

  private def pullApartAny(staticValue: Any, tpe: universe.Type): Any = {
    val result = simpleTypeConversions.get(tpe) match {
      case Some(simpleTypeConversion) => simpleTypeConversion.toDynamic(staticValue)
      case None =>
        //todo: replace conditional with polymorphism
        if (isCaseClass(tpe)) pullApartCaseClass(staticValue, tpe)
        else if (isMap(tpe)) ???
        else if (isSeq(tpe)) pullApartSeq(staticValue.asInstanceOf[Seq[Any]], tpe)
        else throw new RuntimeException("todo: replace conditional with polymorphism")
    }
    result
  }

  private def pullApartCaseClass(value: Any, tpe: universe.Type): Map[String, Any] = {
    val fields: Iterable[universe.TermSymbol] = tpe.decls.map(_.asTerm).filter(_.isGetter)
    val instanceMirror: universe.InstanceMirror = mirror.reflect(value)
    def createEntry(field: universe.TermSymbol): (String, Any) = {
      val fieldName = symbolName(field)
      val fieldMirror: universe.FieldMirror = instanceMirror.reflectField(field)
      val staticFieldValue = fieldMirror.get
      val fieldType = field.typeSignature.resultType
      val dynamicFieldValue = pullApartAny(staticFieldValue, fieldType)
      val entry = (fieldName, dynamicFieldValue)
      entry
    }
    val entries: Iterable[(String, Any)] = fields.map(createEntry)
    val map: Map[String, Any] = entries.toMap
    map
  }

  private def pullApartSeq(staticSeq: Seq[Any], tpe: universe.Type): Seq[Any] = {
    val elementType: universe.Type = tpe.typeArgs.head
    def pullApartElement(element: Any): Any = {
      pullApartAny(element, elementType)
    }
    val dynamicSeq = staticSeq.map(pullApartElement)
    dynamicSeq
  }

  private def isCaseClass(theType: universe.Type): Boolean = {
    val result = theType.baseClasses.map(_.fullName).contains("scala.Product")
    result
  }

  private def isMap(theType: universe.Type): Boolean = {
    val result = theType.baseClasses.map(_.fullName).contains("scala.collection.immutable.Map")
    result
  }

  private def isSeq(theType: universe.Type): Boolean = {
    val result = theType.baseClasses.map(_.fullName).contains("scala.collection.Seq")
    result
  }
}
