package com.seanshubin.utility.reflection

import scala.reflect.runtime._

class ReflectionImpl(simpleTypeConversions: Map[universe.Type, SimpleTypeConversion]) extends Reflection {
  private val mirror: universe.Mirror = universe.runtimeMirror(getClass.getClassLoader)

  override def pieceTogether[T: universe.TypeTag](dynamicValue: Any, staticClass: Class[T]): T = {
    val tpe: universe.Type = universe.typeOf[T]
    val result = pieceTogetherAny(dynamicValue, tpe).asInstanceOf[T]
    result
  }

  def pieceTogetherAny(dynamicValue: Any, tpe: universe.Type): Any = {
    val result = simpleTypeConversions.get(tpe) match {
      case Some(simpleTypeConversion) => simpleTypeConversion.toStatic(dynamicValue.asInstanceOf[String])
      case None => pieceTogetherObject(dynamicValue.asInstanceOf[Map[String, Any]], tpe)
    }
    result
  }

  override def pullApart[T: universe.TypeTag](staticValue: T): Any = {
    val tpe = universe.typeTag[T].tpe
    val dynamicValue = pullApartWithType(staticValue, tpe)
    dynamicValue
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

  private def pullApartWithType(value: Any, tpe: universe.Type): Any = {
    val result = simpleTypeConversions.get(tpe) match {
      case Some(simpleTypeConversion) => simpleTypeConversion.toDynamic(value)
      case None => pullApartObject(value, tpe)
    }
    result
  }

  private def pullApartObject(value: Any, tpe: universe.Type): Map[String, Any] = {
    val fields: Iterable[universe.TermSymbol] = tpe.decls.map(_.asTerm).filter(_.isGetter)
    val instanceMirror: universe.InstanceMirror = mirror.reflect(value)
    def createEntry(field: universe.TermSymbol): (String, Any) = {
      val fieldName = symbolName(field)
      val fieldMirror: universe.FieldMirror = instanceMirror.reflectField(field)
      val staticFieldValue = fieldMirror.get
      val fieldType = field.typeSignature.resultType
      val dynamicFieldValue = pullApartWithType(staticFieldValue, fieldType)
      val entry = (fieldName, dynamicFieldValue)
      entry
    }
    val entries: Iterable[(String, Any)] = fields.map(createEntry)
    val map: Map[String, Any] = entries.toMap
    map
  }
}
