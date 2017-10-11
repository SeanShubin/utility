package com.seanshubin.utility.string

import org.scalatest.FunSuite

class StringUtilTest extends FunSuite {
  test("escape") {
    assert(StringUtil.escape("blah\nblah") === """blah\nblah""")
    assert(StringUtil.escape("blah\bblah") === """blah\bblah""")
    assert(StringUtil.escape("blah\tblah") === """blah\tblah""")
    assert(StringUtil.escape("blah\fblah") === """blah\fblah""")
    assert(StringUtil.escape("blah\rblah") === """blah\rblah""")
    assert(StringUtil.escape("blah\"blah") === """blah\"blah""")
    assert(StringUtil.escape("blah\'blah") === """blah\'blah""")
    assert(StringUtil.escape("blah\\blah") === """blah\\blah""")
  }

  test("unescape") {
    assert(StringUtil.unescape("""blah\nblah""") === "blah\nblah")
    assert(StringUtil.unescape("""blah\bblah""") === "blah\bblah")
    assert(StringUtil.unescape("""blah\tblah""") === "blah\tblah")
    assert(StringUtil.unescape("""blah\fblah""") === "blah\fblah")
    assert(StringUtil.unescape("""blah\rblah""") === "blah\rblah")
    assert(StringUtil.unescape("""blah\"blah""") === "blah\"blah")
    assert(StringUtil.unescape("""blah\'blah""") === "blah\'blah")
    assert(StringUtil.unescape("""blah\\blah""") === "blah\\blah")
  }

  test("to lines"){
    assert(StringUtil.toLines("a\nb\r\nc\rd") === Seq("a", "b", "c", "d"))
  }
}
