package com.seanshubin.utility.string

import org.scalatest.FunSuite

class TableUtilTest extends FunSuite {
  test("table") {
    val input = Seq(
      Seq("Alice", "Bob", "Carol"),
      Seq("Dave", "Eve", "Mallory"),
      Seq("Peggy", "Trent", "Wendy"))
    val expected = Seq(
      "╔═════╤═════╤═══════╗",
      "║Alice│Bob  │Carol  ║",
      "╟─────┼─────┼───────╢",
      "║Dave │Eve  │Mallory║",
      "╟─────┼─────┼───────╢",
      "║Peggy│Trent│Wendy  ║",
      "╚═════╧═════╧═══════╝"
    )
    val actual = TableUtil.createTable(input)
    assert(actual === expected)
  }

  test("left and right justify") {
    val bigInt = BigInt(2)
    val bigDec = BigDecimal(3)
    val input = Seq(
      Seq("left justify column name", "default justification column name", "right justify column name"),
      Seq(TableUtil.LeftJustify("left"), "default", TableUtil.RightJustify("right")),
      Seq(TableUtil.LeftJustify(null), null, TableUtil.RightJustify(null)),
      Seq(TableUtil.LeftJustify(1), 1, TableUtil.RightJustify(1)),
      Seq(TableUtil.LeftJustify(bigInt), bigInt, TableUtil.RightJustify(bigInt)),
      Seq(TableUtil.LeftJustify(bigDec), bigDec, TableUtil.RightJustify(bigDec)))
    val expected = Seq(
      "╔════════════════════════╤═════════════════════════════════╤═════════════════════════╗",
      "║left justify column name│default justification column name│right justify column name║",
      "╟────────────────────────┼─────────────────────────────────┼─────────────────────────╢",
      "║left                    │default                          │                    right║",
      "╟────────────────────────┼─────────────────────────────────┼─────────────────────────╢",
      "║null                    │                             null│                     null║",
      "╟────────────────────────┼─────────────────────────────────┼─────────────────────────╢",
      "║1                       │                                1│                        1║",
      "╟────────────────────────┼─────────────────────────────────┼─────────────────────────╢",
      "║2                       │                                2│                        2║",
      "╟────────────────────────┼─────────────────────────────────┼─────────────────────────╢",
      "║3                       │                                3│                        3║",
      "╚════════════════════════╧═════════════════════════════════╧═════════════════════════╝"
    )
    val actual = TableUtil.createTable(input)
    assert(actual === expected)
  }
}
