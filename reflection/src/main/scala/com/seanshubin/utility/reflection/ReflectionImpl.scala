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
      case None => createComplex(tpe).pieceTogetherAny(dynamicValue, tpe)
    }
    result
  }

  private def pieceTogetherCaseClass(valueMap: Map[String, Any], tpe: universe.Type): Any = {
    val constructor: universe.MethodSymbol = tpe.decl(universe.termNames.CONSTRUCTOR).asMethod
    val constructorParameters: Seq[universe.TermSymbol] = constructor.typeSignature.paramLists.head.map(_.asTerm)
    val parameterList: Seq[Any] = createParameterList(constructorParameters, valueMap)
    val typeClass: universe.ClassSymbol = tpe.typeSymbol.asClass
    val classMirror: universe.ClassMirror = mirror.reflectClass(typeClass)
    val constructorMethod: universe.MethodMirror = classMirror.reflectConstructor(constructor)
    val constructed: Any = constructorMethod(parameterList: _*)
    constructed
  }

  private def pieceTogetherSeq(dynamicSeq: Seq[Any], tpe: universe.Type): Seq[Any] = {
    val elementType: universe.Type = tpe.typeArgs.head
    def pieceTogetherElement(dynamicValue: Any): Any = {
      pieceTogetherAny(dynamicValue, elementType)
    }
    val staticSeq = dynamicSeq.map(pieceTogetherElement)
    staticSeq
  }

  private def pieceTogetherMap(dynamicMap: Map[Any, Any], tpe: universe.Type): Map[Any, Any] = {
    val keyElementType: universe.Type = tpe.typeArgs(0)
    val valueElementType: universe.Type = tpe.typeArgs(1)
    def pieceTogetherEntry(dynamicEntry: (Any, Any)): (Any, Any) = {
      val (dynamicKey, dynamicValue) = dynamicEntry
      val staticKey = pieceTogetherAny(dynamicKey, keyElementType)
      val staticValue = pieceTogetherAny(dynamicValue, valueElementType)
      (staticKey, staticValue)
    }
    val staticMap = dynamicMap.map(pieceTogetherEntry)
    staticMap
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
      case None => createComplex(tpe).pullApartAny(staticValue, tpe)
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

  private def pullApartMap(staticMap: Map[Any, Any], tpe: universe.Type): Map[Any, Any] = {
    val keyElementType: universe.Type = tpe.typeArgs(0)
    val valueElementType: universe.Type = tpe.typeArgs(1)
    def pullApartEntry(staticEntry: (Any, Any)): (Any, Any) = {
      val (staticKey, staticValue) = staticEntry
      val dynamicKey = pullApartAny(staticKey, keyElementType)
      val dynamicValue = pullApartAny(staticValue, valueElementType)
      (dynamicKey, dynamicValue)
    }
    val dynamicMap = staticMap.map(pullApartEntry)
    dynamicMap
  }

  private def createComplex(theType: universe.Type): Complex = {
    val fullNames = theType.baseClasses.map(_.fullName)
    if (fullNames.contains("scala.Product")) ComplexCaseClass
    else if (fullNames.contains("scala.collection.immutable.Map")) ComplexMap
    else if (fullNames.contains("scala.collection.Seq")) ComplexSeq
    else throw new RuntimeException(s"Unsupported type: $theType")
  }

  private sealed trait Complex {
    def pullApartAny(staticValue: Any, tpe: universe.Type): Any

    def pieceTogetherAny(dynamicValue: Any, tpe: universe.Type): Any
  }

  private object ComplexCaseClass extends Complex {
    override def pullApartAny(staticValue: Any, tpe: universe.Type): Any =
      pullApartCaseClass(staticValue, tpe)

    override def pieceTogetherAny(dynamicValue: Any, tpe: universe.Type): Any =
      pieceTogetherCaseClass(dynamicValue.asInstanceOf[Map[String, Any]], tpe)
  }

  private object ComplexSeq extends Complex {
    override def pullApartAny(staticValue: Any, tpe: universe.Type): Any =
      pullApartSeq(staticValue.asInstanceOf[Seq[Any]], tpe)

    override def pieceTogetherAny(dynamicValue: Any, tpe: universe.Type): Any =
      pieceTogetherSeq(dynamicValue.asInstanceOf[Seq[Any]], tpe)
  }

  private object ComplexMap extends Complex {
    override def pullApartAny(staticValue: Any, tpe: universe.Type): Any =
      pullApartMap(staticValue.asInstanceOf[Map[Any, Any]], tpe)

    override def pieceTogetherAny(dynamicValue: Any, tpe: universe.Type): Any =
      pieceTogetherMap(dynamicValue.asInstanceOf[Map[Any, Any]], tpe)
  }

}