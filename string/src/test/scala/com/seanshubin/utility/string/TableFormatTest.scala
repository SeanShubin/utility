package com.seanshubin.utility.string

import com.seanshubin.utility.collection.SeqDifference
import com.seanshubin.utility.string.TableFormat.{LeftJustify, RightJustify}
import org.scalatest.FunSuite

class TableFormatTest extends FunSuite {
  test("box drawing characters") {
    val tableFormat = TableFormat.BoxDrawingCharacters
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
    val actual = tableFormat.createTable(input)
    assertLinesEqual(actual, expected)
  }

  test("plain text characters") {
    val tableFormat = TableFormat.AsciiDrawingCharacters
    val input = Seq(
      Seq("Alice", "Bob", "Carol"),
      Seq("Dave", "Eve", "Mallory"),
      Seq("Peggy", "Trent", "Wendy"))
    val expected = Seq(
      "/-----+-----+-------\\",
      "|Alice|Bob  |Carol  |",
      "+-----+-----+-------+",
      "|Dave |Eve  |Mallory|",
      "+-----+-----+-------+",
      "|Peggy|Trent|Wendy  |",
      "\\-----+-----+-------/"
    )
    val actual = tableFormat.createTable(input)
    assertLinesEqual(actual, expected)
  }

  test("compact") {
    val tableFormat = TableFormat.CompactDrawingCharacters
    val input = Seq(
      Seq("Alice", "Bob", "Carol"),
      Seq("Dave", "Eve", "Mallory"),
      Seq("Peggy", "Trent", "Wendy"))
    val expected = Seq(
      "Alice Bob   Carol  ",
      "Dave  Eve   Mallory",
      "Peggy Trent Wendy  "
    )
    val actual = tableFormat.createTable(input)
    assertLinesEqual(actual, expected)
  }

  test("left and right justify") {
    import TableFormat.{LeftJustify, RightJustify}
    val tableFormat = TableFormat.BoxDrawingCharacters
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
    val actual = tableFormat.createTable(input)
    assertLinesEqual(actual, expected)
  }

  test("left and right justify something small") {
    val tableFormat = TableFormat.BoxDrawingCharacters
    assert(tableFormat.createTable(Seq(Seq("a"))) === Seq("╔═╗", "║a║", "╚═╝"))
    assert(tableFormat.createTable(Seq(Seq(LeftJustify("a")))) === Seq("╔═╗", "║a║", "╚═╝"))
    assert(tableFormat.createTable(Seq(Seq(RightJustify("a")))) === Seq("╔═╗", "║a║", "╚═╝"))
  }

  test("no columns") {
    val tableFormat = TableFormat.BoxDrawingCharacters
    assert(tableFormat.createTable(Seq(Seq())) === Seq("╔╗", "║║", "╚╝"))
  }

  test("no rows") {
    val tableFormat = TableFormat.BoxDrawingCharacters
    assert(tableFormat.createTable(Seq()) === Seq("╔╗", "╚╝"))
  }

  test("replace empty cells with blank cells") {
    val tableFormat = TableFormat.BoxDrawingCharacters
    val input = Seq(
      Seq("Alice", "Bob", "Carol"),
      Seq("Dave", "Eve"),
      Seq("Peggy", "Trent", "Wendy"))
    val expected = Seq(
      "╔═════╤═════╤═════╗",
      "║Alice│Bob  │Carol║",
      "╟─────┼─────┼─────╢",
      "║Dave │Eve  │     ║",
      "╟─────┼─────┼─────╢",
      "║Peggy│Trent│Wendy║",
      "╚═════╧═════╧═════╝"
    )
    val actual = tableFormat.createTable(input)
    assertLinesEqual(actual, expected)
  }

  def assertLinesEqual(actual: Seq[Any], expected: Seq[Any]): Unit = {
    val seqDifference = SeqDifference.diff(actual, expected)
    assert(seqDifference.isSame, seqDifference.messageLines.mkString("\n"))
  }
}
