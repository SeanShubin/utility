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
    import TableUtil.LeftJustify
    import TableUtil.RightJustify
    val bigInt = BigInt(2)
    val bigDec = BigDecimal(3)
    val input = Seq(
      Seq("left justify column name", "default justification column name", "right justify column name"),
      Seq(LeftJustify("left"), "default", RightJustify("right")),
      Seq(LeftJustify(null), null, RightJustify(null)),
      Seq(LeftJustify(1), 1, RightJustify(1)),
      Seq(LeftJustify(bigInt), bigInt, RightJustify(bigInt)),
      Seq(LeftJustify(bigDec), bigDec, RightJustify(bigDec)))
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
