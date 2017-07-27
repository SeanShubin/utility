package com.seanshubin.utility.string

object TableUtil {

  sealed trait Justify

  case class LeftJustify(x: Any) extends Justify

  case class RightJustify(x: Any) extends Justify

  def createTable(rows: Seq[Seq[Any]]): Seq[String] = {
    val columns = rows.transpose
    val columnWidths = columns.map(maxWidthForColumn)
    val top = makeTop(columnWidths)
    val middle = makeMiddle(columnWidths)
    val bottom = makeBottom(columnWidths)
    val formattedRows = formatRows(columnWidths, rows)
    Seq(top) ++ interleave(formattedRows, middle) ++ Seq(bottom)
  }

  private val emptyRow = Stream.continually("")

  private def makeTop(columnWidths: Seq[Int]): String = {
    makeRow(columnWidths, emptyRow, "═", "╔", "╤", "╗")
  }

  private def makeMiddle(columnWidths: Seq[Int]): String = {
    makeRow(columnWidths, emptyRow, "─", "╟", "┼", "╢")
  }

  private def makeBottom(columnWidths: Seq[Int]): String = {
    makeRow(columnWidths, emptyRow, "═", "╚", "╧", "╝")
  }

  private def makeRow(columnWidths: Seq[Int], data: Seq[Any], padding: String, left: String, center: String, right: String): String = {
    val formattedCells = for {
      (width, cell) <- columnWidths zip data
    } yield {
      formatCell(cell, width, padding)
    }
    formattedCells.mkString(left, center, right)
  }

  private def formatRows(columnWidths: Seq[Int], rows: Seq[Seq[Any]]): Seq[String] = {
    val formatRow = makeRow(columnWidths, _: Seq[Any], " ", "║", "│", "║")
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

  private def rightJustify(s: String, width: Int, padding: String): String = {
    paddingFor(s, width, padding) + s
  }

  private def leftJustify(s: String, width: Int, padding: String): String = {
    s + paddingFor(s, width, padding)
  }

  private def paddingFor(s: String, width: Int, padding: String): String = {
    val quantity = width - s.length
    padding * quantity
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
}
