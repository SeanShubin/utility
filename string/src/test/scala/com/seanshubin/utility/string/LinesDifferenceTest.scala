package com.seanshubin.utility.string

import org.scalatest.FunSuite

class LinesDifferenceTest extends FunSuite {
  test("empty") {
    val linesDifference = LinesDifference.compare("", "")
    assert(linesDifference.isSame === true)
    assert(linesDifference.detailLines === Seq("(1) "))
  }

  test("same lines") {
    val linesDifference = LinesDifference.compare("aaa\nbbb\nccc", "aaa\nbbb\nccc")
    assert(linesDifference.isSame === true)
    assert(linesDifference.detailLines === Seq("(1) aaa", "(2) bbb", "(3) ccc"))
  }

  test("mac style newlines") {
    val linesDifference = LinesDifference.compare("ddd\reee\rfff", "ddd\reee\rfff")
    assert(linesDifference.isSame === true)
    assert(linesDifference.detailLines === Seq("(1) ddd", "(2) eee", "(3) fff"))
  }

  test("windows style newlines") {
    val linesDifference = LinesDifference.compare("ggg\r\nhhh\r\niii", "ggg\r\nhhh\r\niii")
    assert(linesDifference.isSame === true)
    assert(linesDifference.detailLines === Seq("(1) ggg", "(2) hhh", "(3) iii"))
  }

  test("different line") {
    val linesDifference = LinesDifference.compare("aaa\nbbb\nccc", "aaa\nddd\nccc")
    assert(linesDifference.isSame === false)
    assert(linesDifference.detailLines === Seq("(1) aaa", "(2) bbb", "(2) ddd"))
  }

  test("missing line") {
    val linesDifference = LinesDifference.compare("aaa\nbbb\nccc", "aaa\nbbb")
    assert(linesDifference.isSame === false)
    assert(linesDifference.detailLines === Seq("(1) aaa", "(2) bbb", "(3) ccc", "<missing>"))
  }

  test("extra line") {
    val linesDifference = LinesDifference.compare("aaa\nbbb\nccc", "aaa\nbbb\nccc\nddd")
    assert(linesDifference.isSame === false)
    assert(linesDifference.detailLines === Seq("(1) aaa", "(2) bbb", "(3) ccc", "<missing>", "(4) ddd"))
  }

  test("padding") {
    val lines = Seq.fill(10)("aaa").mkString("\n")
    val linesDifference = LinesDifference.compare(lines, lines)
    assert(linesDifference.isSame === true)
    assert(linesDifference.detailLines.size === 10)
    assert(linesDifference.detailLines.head === "( 1) aaa")
  }
}
