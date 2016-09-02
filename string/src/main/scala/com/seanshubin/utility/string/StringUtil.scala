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

  def hex(target: Seq[Byte]): String = {
    target.map(hex).mkString
  }

  def hex(target: Byte): String = {
    val digits = "0123456789ABCDEF"
    "" + digits(target >> 4 & 15) + digits(target & 15)
  }
}
