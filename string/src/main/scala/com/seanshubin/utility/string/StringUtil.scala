package com.seanshubin.utility.string

object StringUtil {
  def escape(target: String) = {
    target.flatMap {
      case '\n' => "\\n"
      case '\b' => "\\b"
      case '\t' => "\\t"
      case '\f' => "\\f"
      case '\r' => "\\r"
      case '\"' => "\\\""
      case '\'' => "\\\'"
      case '\\' => "\\\\"
      case x => x.toString
    }
  }

  def doubleQuote(target: String) = s""""${escape(target)}""""

  def unescape(target: String): String = {
    target.
      replaceAll( """\\n""", "\n").
      replaceAll( """\\b""", "\b").
      replaceAll( """\\t""", "\t").
      replaceAll( """\\f""", "\f").
      replaceAll( """\\r""", "\r").
      replaceAll( """\\"""", "\"").
      replaceAll( """\\'""", "\'").
      replaceAll( """\\\\""", "\\")
  }

  def bytesToHex(target: Seq[Byte]): String = {
    target.map(byteToHex).mkString
  }

  def byteToHex(target: Byte): String = {
    val digits = "0123456789ABCDEF"
    "" + digits(target >> 4 & 15) + digits(target & 15)
  }

  def hexToBytes(target:String):Seq[Byte] = {
    target.grouped(2).map(hexToByte).toSeq
  }

  def hexToByte(rawTarget:String):Byte = {
    if(rawTarget.length != 2) throw new RuntimeException("hexToByte expectes exactly 2 characters")
    val target = rawTarget.toUpperCase
    val numericTuples = 0 to 9 map (x => x.toString.head -> x)
    val alphaTuples = 'A' to 'F' map (x => x -> (x - 'A' + 10))
    val digitsMap = (numericTuples ++ alphaTuples).toMap
    val firstDigit = digitsMap(target(0))
    val secondDigit = digitsMap(target(1))
    val result = firstDigit * 16 + secondDigit
    result.toByte
  }
}
