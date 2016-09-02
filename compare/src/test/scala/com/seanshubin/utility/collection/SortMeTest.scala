package com.seanshubin.utility.collection

/*
How to sort case classes in Scala

This sample demonstrates sorting case classes in different directions by fields in different orders

For example, the SortMe case class has id and name
- id can be ascending or descending
- name can be ascending or descending
- we can sort by id then name, or by name then id

This leaves 8 combinations, all demonstrated below

Can anybody figure out a better way to do this?  If so, let me know.
 */

import org.scalatest.FunSuite

class SortMeTest extends FunSuite {
  val unsorted = Seq(
    SortMe(2, "b"),
    SortMe(3, "c"),
    SortMe(1, "a"),
    SortMe(2, "c"),
    SortMe(2, "a"),
    SortMe(3, "a"),
    SortMe(1, "b"),
    SortMe(3, "b"),
    SortMe(1, "c"))

  test("id ascending first, then name ascending") {
    val sorted = unsorted.sorted(SortMe.OrderingIdAscendingNameAscending)
    assert(sorted === Seq(
      SortMe(1, "a"),
      SortMe(1, "b"),
      SortMe(1, "c"),
      SortMe(2, "a"),
      SortMe(2, "b"),
      SortMe(2, "c"),
      SortMe(3, "a"),
      SortMe(3, "b"),
      SortMe(3, "c")
    ))
  }
  test("id ascending first, then name descending") {
    val sorted = unsorted.sorted(SortMe.OrderingIdAscendingNameDescending)
    assert(sorted === Seq(
      SortMe(1, "c"),
      SortMe(1, "b"),
      SortMe(1, "a"),
      SortMe(2, "c"),
      SortMe(2, "b"),
      SortMe(2, "a"),
      SortMe(3, "c"),
      SortMe(3, "b"),
      SortMe(3, "a")
    ))
  }
  test("id descending first, then name ascending") {
    val sorted = unsorted.sorted(SortMe.OrderingIdDescendingNameAscending)
    assert(sorted === Seq(
      SortMe(3, "a"),
      SortMe(3, "b"),
      SortMe(3, "c"),
      SortMe(2, "a"),
      SortMe(2, "b"),
      SortMe(2, "c"),
      SortMe(1, "a"),
      SortMe(1, "b"),
      SortMe(1, "c")
    ))
  }
  test("id descending first, then name descending") {
    val sorted = unsorted.sorted(SortMe.OrderingIdDescendingNameDescending)
    assert(sorted === Seq(
      SortMe(3, "c"),
      SortMe(3, "b"),
      SortMe(3, "a"),
      SortMe(2, "c"),
      SortMe(2, "b"),
      SortMe(2, "a"),
      SortMe(1, "c"),
      SortMe(1, "b"),
      SortMe(1, "a")
    ))
  }
  test("name ascending first, then id ascending") {
    val sorted = unsorted.sorted(SortMe.OrderingNameAscendingIdAscending)
    assert(sorted === Seq(
      SortMe(1, "a"),
      SortMe(2, "a"),
      SortMe(3, "a"),
      SortMe(1, "b"),
      SortMe(2, "b"),
      SortMe(3, "b"),
      SortMe(1, "c"),
      SortMe(2, "c"),
      SortMe(3, "c")
    ))
  }
  test("name descending first, then id ascending") {
    val sorted = unsorted.sorted(SortMe.OrderingNameDescendingIdAscending)
    assert(sorted === Seq(
      SortMe(1, "c"),
      SortMe(2, "c"),
      SortMe(3, "c"),
      SortMe(1, "b"),
      SortMe(2, "b"),
      SortMe(3, "b"),
      SortMe(1, "a"),
      SortMe(2, "a"),
      SortMe(3, "a")
    ))
  }
  test("name ascending first, then id descending") {
    val sorted = unsorted.sorted(SortMe.OrderingNameAscendingIdDescending)
    assert(sorted === Seq(
      SortMe(3, "a"),
      SortMe(2, "a"),
      SortMe(1, "a"),
      SortMe(3, "b"),
      SortMe(2, "b"),
      SortMe(1, "b"),
      SortMe(3, "c"),
      SortMe(2, "c"),
      SortMe(1, "c")
    ))
  }
  test("name descending first, then id descending") {
    val sorted = unsorted.sorted(SortMe.OrderingNameDescendingIdDescending)
    assert(sorted === Seq(
      SortMe(3, "c"),
      SortMe(2, "c"),
      SortMe(1, "c"),
      SortMe(3, "b"),
      SortMe(2, "b"),
      SortMe(1, "b"),
      SortMe(3, "a"),
      SortMe(2, "a"),
      SortMe(1, "a")
    ))
  }
}
