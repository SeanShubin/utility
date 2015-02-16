package com.seanshubin.utility.reflection

import scala.reflect.runtime.universe

trait Reflection {
  def pieceTogether[T: universe.TypeTag](dynamicValue: Any, staticClass: Class[T]): T

  def pullApart[T: universe.TypeTag](staticValue: T): Any
}
