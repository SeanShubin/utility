package com.seanshubin.utility.reflection

import java.time.{Instant, ZonedDateTime}

import scala.reflect.runtime._

trait SimpleTypeConversion {
  def toDynamic(x: Any): String

  def toStatic(x: String): Any
}

object SimpleTypeConversion {

  class ByteConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Byte].toString

    override def toStatic(x: String): Any = x.toByte
  }

  class ShortConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Short].toString

    override def toStatic(x: String): Any = x.toShort
  }

  class CharConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Char].toString

    override def toStatic(x: String): Any = x.asInstanceOf[String].charAt(0)
  }

  class IntConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Int].toString

    override def toStatic(x: String): Any = x.toInt
  }

  class LongConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Long].toString

    override def toStatic(x: String): Any = x.toLong
  }

  class FloatConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Float].toString

    override def toStatic(x: String): Any = x.toFloat
  }

  class DoubleConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Double].toString

    override def toStatic(x: String): Any = x.toDouble
  }

  class BooleanConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Boolean].toString

    override def toStatic(x: String): Any = x.toBoolean
  }

  class UnitConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Unit].toString

    override def toStatic(x: String): Any = ()
  }

  class NullConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = "" + x.asInstanceOf[Null]

    override def toStatic(x: String): Any = null
  }

  class StringConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[String]

    override def toStatic(x: String): Any = x
  }

  class BigIntConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[BigInt].toString()

    override def toStatic(x: String): Any = BigInt(x)
  }

  class BigDecimalConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[BigDecimal].toString()

    override def toStatic(x: String): Any = BigDecimal(x)
  }

  class ZonedDateTimeConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[ZonedDateTime].toString

    override def toStatic(x: String): Any = ZonedDateTime.parse(x)
  }

  class InstantConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Instant].toString

    override def toStatic(x: String): Any = Instant.parse(x)
  }

  val defaultConversions: Map[universe.Type, SimpleTypeConversion] = Map(
    universe.TypeTag.Byte.tpe -> new ByteConversion,
    universe.TypeTag.Short.tpe -> new ShortConversion,
    universe.TypeTag.Char.tpe -> new CharConversion,
    universe.TypeTag.Int.tpe -> new IntConversion,
    universe.TypeTag.Long.tpe -> new LongConversion,
    universe.TypeTag.Float.tpe -> new FloatConversion,
    universe.TypeTag.Double.tpe -> new DoubleConversion,
    universe.TypeTag.Boolean.tpe -> new BooleanConversion,
    universe.TypeTag.Unit.tpe -> new UnitConversion,
    universe.TypeTag.Null.tpe -> new NullConversion,
    universe.typeTag[String].tpe -> new StringConversion,
    universe.typeTag[BigInt].tpe -> new BigIntConversion,
    universe.typeTag[BigDecimal].tpe -> new BigDecimalConversion,
    universe.typeTag[ZonedDateTime].tpe -> new ZonedDateTimeConversion,
    universe.typeTag[Instant].tpe -> new InstantConversion
  )
}
