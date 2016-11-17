package com.seanshubin.utility.http

import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

import org.scalatest.FunSuite

class RegexTest extends FunSuite {
  val bytes: Seq[Byte] = (0 to 127).map(_.toByte)
  val sampleString = new String(bytes.toArray, StandardCharsets.US_ASCII)
  val regexMap = Map(
    "CHAR" -> """[\x00-\x7F]""",
    "ALPHA" -> """[A-Za-z]""",
    "UPALPHA" -> """[A-Z]""",
    "LOALPHA" -> """[a-z]""",
    "DIGIT" -> """\d""",
    "CTL" -> """[\x00-\x1F\x7F]""",
    "CR" -> """\r""",
    "LF" -> """\n""",
    "SP" -> """ """,
    "HT" -> """\t""",
    "<\">" -> "\""
  )
  test("foo") {
        verify(name = "CHAR", expectedMatches = 128, samples = '\n', 'A', '$')
        verify(name = "UPALPHA", expectedMatches = 26, samples = 'A', 'B', 'Z')
        verify(name = "LOALPHA", expectedMatches = 26, samples = 'a', 'b', 'z')
        verify(name = "ALPHA", expectedMatches = 52, samples = 'a', 'B', 'z')
        verify(name = "DIGIT", expectedMatches = 10, samples = '0', '1', '9')
        verify(name = "CTL", expectedMatches = 33, samples = '\u0000', 31, 127)
        verify(name = "CR", expectedMatches = 1, samples = 13)
        verify(name = "LF", expectedMatches = 1, samples = 10)
        verify(name = "SP", expectedMatches = 1, samples = 32)
        verify(name = "HT", expectedMatches = 1, samples = 9)
        verify(name = "<\">", expectedMatches = 1, samples = 34)

  }

  def verify(name: String, expectedMatches: Int, samples: Char*): Unit = {
    val pattern = Pattern.compile(regexMap(name))
    def isMatch(sample: Char): Boolean = {
      pattern.matcher(sample.toString).matches()
    }
    def assertMatch(sample: Char): Unit = {
      assert(pattern.matcher(sample.toString).matches(), name + ": " + sample)
    }
    val actualMatches = sampleString.count(isMatch)
    assert(actualMatches === expectedMatches, name)

    samples.foreach(assertMatch)
  }
}

/*
https://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html#sec2.2
2.2 Basic Rules

OCTET          = <any 8-bit sequence of data>
CHAR           = <any US-ASCII character (octets 0 - 127)>
UPALPHA        = <any US-ASCII uppercase letter "A".."Z">
LOALPHA        = <any US-ASCII lowercase letter "a".."z">
ALPHA          = UPALPHA | LOALPHA
DIGIT          = <any US-ASCII digit "0".."9">
CTL            = <any US-ASCII control character (octets 0 - 31) and DEL (127)>
CR             = <US-ASCII CR, carriage return (13)>
LF             = <US-ASCII LF, linefeed (10)>
SP             = <US-ASCII SP, space (32)>
HT             = <US-ASCII HT, horizontal-tab (9)>
<">            = <US-ASCII double-quote mark (34)>

CRLF           = CR LF
LWS            = [CRLF] 1*( SP | HT )
TEXT           = <any OCTET except CTLs, but including LWS>
HEX            = "A" | "B" | "C" | "D" | "E" | "F" | "a" | "b" | "c" | "d" | "e" | "f" | DIGIT
token          = 1*<any CHAR except CTLs or separators>
separators     = "(" | ")" | "<" | ">" | "@"
               | "," | ";" | ":" | "\" | <">
               | "/" | "[" | "]" | "?" | "="
               | "{" | "}" | SP | HT
comment        = "(" *( ctext | quoted-pair | comment ) ")"
ctext          = <any TEXT excluding "(" and ")">
quoted-string  = ( <"> *(qdtext | quoted-pair ) <"> )
qdtext         = <any TEXT except <">>
quoted-pair    = "\" CHAR

 */