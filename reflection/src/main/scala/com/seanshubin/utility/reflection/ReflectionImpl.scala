package com.seanshubin.utility.reflection

import scala.reflect.runtime._

class ReflectionImpl(simpleTypeConversions: Map[universe.Type, SimpleTypeConversion]) extends Reflection {
  override def pieceTogether[T: universe.TypeTag](dynamicValue: Any, staticClass: Class[T]): T = {
    val tpe: universe.Type = universe.typeOf[T]
    val simpleTypeConversion: SimpleTypeConversion = simpleTypeConversions(tpe)
    simpleTypeConversion.toStatic(dynamicValue.asInstanceOf[String]).asInstanceOf[T]
  }

  override def pullApart(staticValue: Any): Any = {
    "" + staticValue
  }
}
