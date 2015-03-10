package com.seanshubin.utility.reflection

import java.nio.file.{Path, Paths}
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

  class PathConversion extends SimpleTypeConversion {
    override def toDynamic(x: Any): String = x.asInstanceOf[Path].toString

    override def toStatic(x: String): Any = Paths.get(x)
  }

  val defaultConversions: Map[String, SimpleTypeConversion] = Map(
    universe.TypeTag.Byte.tpe.toString -> new ByteConversion,
    universe.TypeTag.Short.tpe.toString -> new ShortConversion,
    universe.TypeTag.Char.tpe.toString -> new CharConversion,
    universe.TypeTag.Int.tpe.toString -> new IntConversion,
    universe.TypeTag.Long.tpe.toString -> new LongConversion,
    universe.TypeTag.Float.tpe.toString -> new FloatConversion,
    universe.TypeTag.Double.tpe.toString -> new DoubleConversion,
    universe.TypeTag.Boolean.tpe.toString -> new BooleanConversion,
    universe.TypeTag.Unit.tpe.toString -> new UnitConversion,
    universe.TypeTag.Null.tpe.toString -> new NullConversion,
    universe.typeTag[String].tpe.toString -> new StringConversion,
    universe.typeTag[BigInt].tpe.toString -> new BigIntConversion,
    universe.typeTag[BigDecimal].tpe.toString -> new BigDecimalConversion,
    universe.typeTag[ZonedDateTime].tpe.toString -> new ZonedDateTimeConversion,
    universe.typeTag[Instant].tpe.toString -> new InstantConversion,
    universe.typeTag[Path].tpe.toString -> new PathConversion
  )
}
