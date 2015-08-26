package com.seanshubin.utility.collection

import org.scalatest.FunSuite

class CollectionDiffTest extends FunSuite {
  test("same") {
    val a = Seq("a", "b", "c")
    val b = Seq("a", "b", "c")
    val actual = CollectionDiff.compare(a, b)
    assert(actual.isSame === true)
    assert(actual.added === Seq())
    assert(actual.removed === Seq())
  }

//  test("one added") {
//    val a = Seq("a", "b", "c")
//    val b = Seq("a", "b", "added", "c")
//    val actual = CollectionDiff.compare(a, b)
//    assert(actual.isSame === false)
//    assert(actual.added === Seq("added"))
//    assert(actual.removed === Seq())
//  }
}
