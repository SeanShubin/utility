package com.seanshubin.utility.string

import com.seanshubin.utility.string.TableFormat._

class TableFormat(val contentSeparators: Content, val horizontalSeparators: Option[HorizontalSeparators]) {
  def this(content: Content) = {
    this(content, None)
  }

  def this(content: Content, top: Top, middle: Middle, bottom: Bottom) = {
    this(content, Some(HorizontalSeparators(top, middle, bottom)))
  }

  def format(originalRows: Seq[Seq[Any]]): Seq[String] = {
    val paddedRows = makeAllRowsTheSameSize(originalRows, "")
    val columns = paddedRows.transpose
    val columnWidths = columns.map(maxWidthForColumn)
    val formattedRows = formatRows(columnWidths, paddedRows, contentSeparators)
    horizontalSeparators match {
      case Some(HorizontalSeparators(topSeparators, middleSeparators, bottomSeparators)) =>
        val emptyRow = Stream.continually("")
        val top = makeRow(columnWidths, emptyRow, topSeparators)
        val middle = makeRow(columnWidths, emptyRow, middleSeparators)
        val bottom = makeRow(columnWidths, emptyRow, bottomSeparators)
        Seq(top) ++ interleave(formattedRows, middle) ++ Seq(bottom)
      case None =>
        formattedRows
    }
  }
}

object TableFormat {
  val BoxDrawingCharacters = new TableFormat(Content(" ", "║", "│", "║"), Top("═", "╔", "╤", "╗"), Middle("─", "╟", "┼", "╢"), Bottom("═", "╚", "╧", "╝"))
  val AsciiDrawingCharacters = new TableFormat(Content(" ", "|", "|", "|"), Top("-", "/", "+", "\\"), Middle("-", "+", "+", "+"), Bottom("-", "\\", "+", "/"))
  val CompactDrawingCharacters = new TableFormat(Content(" ", "", " ", ""))


  trait VerticalSeparators {
    def padding: String

    def left: String

    def center: String

    def right: String
  }

  case class Content(padding: String, left: String, center: String, right: String) extends VerticalSeparators

  case class Top(padding: String, left: String, center: String, right: String) extends VerticalSeparators

  case class Middle(padding: String, left: String, center: String, right: String) extends VerticalSeparators

  case class Bottom(padding: String, left: String, center: String, right: String) extends VerticalSeparators

  case class HorizontalSeparators(top: Top, middle: Middle, bottom: Bottom)

  sealed trait Justify

  case class LeftJustify(x: Any) extends Justify

  case class RightJustify(x: Any) extends Justify

  private def makeAllRowsTheSameSize(rows: Seq[Seq[Any]], value: Any): Seq[Seq[Any]] = {
    val rowSizes = rows.map(_.size)
    val targetSize = if (rowSizes.isEmpty) 0 else rowSizes.max

    def makeRowSameSize(row: Seq[Any]): Seq[Any] = {
      val extraCells = makeExtraCells(targetSize - row.size, value)
      row ++ extraCells
    }

    val sameSizeRows = rows.map(makeRowSameSize)
    sameSizeRows
  }

  private def makeExtraCells(howMany: Int, contents: Any): Seq[Any] = {
    (1 to howMany).map(_ => contents)
  }

  private val emptyRow = Stream.continually("")

  private def makeRow(columnWidths: Seq[Int], data: Seq[Any], verticalSeparators: VerticalSeparators): String = {
    val formattedCells = for {
      (width, cell) <- columnWidths zip data
    } yield {
      formatCell(cell, width, verticalSeparators.padding)
    }
    formattedCells.mkString(verticalSeparators.left, verticalSeparators.center, verticalSeparators.right)
  }

  private def formatRows(columnWidths: Seq[Int], rows: Seq[Seq[Any]], verticalSeparators: VerticalSeparators): Seq[String] = {
    val formatRow = makeRow(columnWidths, _: Seq[Any], verticalSeparators)
    rows.map(formatRow)
  }

  private def formatCell(cell: Any, width: Int, padding: String): String = {
    cell match {
      case LeftJustify(x) => leftJustify(cellToString(x), width, padding)
      case RightJustify(x) => rightJustify(cellToString(x), width, padding)
      case null => rightJustify(cellToString(cell), width, padding)
      case _: String => leftJustify(cellToString(cell), width, padding)
      case _ => rightJustify(cellToString(cell), width, padding)
    }
  }

  private def interleave[T](data: Seq[T], separator: T): Seq[T] = {
    def combine(soFar: List[T], next: T): List[T] = {
      next :: separator :: soFar
    }

    if (data.isEmpty) {
      Seq()
    } else {
      data.tail.foldLeft(List(data.head))(combine).reverse
    }
  }

  private def maxWidthForColumn(column: Seq[Any]): Int = {
    column.map(cellWidth).max
  }

  private def cellWidth(cell: Any): Int = {
    cellToString(cell).length
  }

  private def cellToString(cell: Any): String = {
    cell match {
      case null => "null"
      case LeftJustify(x) => cellToString(x)
      case RightJustify(x) => cellToString(x)
      case x => x.toString
    }
  }

  private def rightJustify(s: String, width: Int, padding: String = " "): String = {
    paddingFor(s, width, padding) + s
  }

  private def leftJustify(s: String, width: Int, padding: String = " "): String = {
    s + paddingFor(s, width, padding)
  }

  private def paddingFor(s: String, width: Int, padding: String): String = {
    val quantity = width - s.length
    padding * quantity
  }
}
