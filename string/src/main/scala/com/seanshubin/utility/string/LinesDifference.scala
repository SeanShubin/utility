package com.seanshubin.utility.string

import scala.annotation.tailrec

object LinesDifference {
  def compare(left: String, right: String): LinesDifferenceResult = {
    val leftLines = breakLines(left)
    val rightLines = breakLines(right)
    compareLines(Nil, leftLines, rightLines)
  }

  @tailrec
  private def compareLines(sameSoFar: List[String], leftLines: List[String], rightLines: List[String]): LinesDifferenceResult = {
    (leftLines.headOption, rightLines.headOption) match {
      case (None, None) => new Same(attachLineNumbers(sameSoFar.reverse))
      case (Some(left), None) => new Difference(attachLineNumbers((left :: sameSoFar).reverse) :+ "<missing>")
      case (None, Some(right)) =>
        val formattedLines = attachLineNumbers((right :: sameSoFar).reverse)
        val reversed = formattedLines.toList.reverse
        val withMissing = reversed.head :: "<missing>" :: reversed.tail
        new Difference(withMissing.reverse)
      case (Some(left), Some(right)) =>
        if (left == right) {
          compareLines(left :: sameSoFar, leftLines.tail, rightLines.tail)
        } else {
          val soFarLines = sameSoFar.reverse.zipWithIndex
          val differentLeft = (left, sameSoFar.size)
          val differentRight = (right, sameSoFar.size)
          val formattedLineNumbers = formatLineNumbers(soFarLines :+ differentLeft :+ differentRight)
          new Difference(
            formattedLineNumbers
          )
        }
    }
  }

  private def attachLineNumbers(lines: Seq[String]): Seq[String] = {
    val indexedLines: Seq[(String, Int)] = lines.zipWithIndex
    formatLineNumbers(indexedLines)
  }

  private def formatLineNumbers(indexedLines: Seq[(String, Int)]): Seq[String] = {
    val formatLine: ((String, Int)) => String = createLineFormatter(indexedLines.size)
    val linesWithNumbers: Seq[String] = indexedLines.map(formatLine)
    linesWithNumbers
  }

  private def createLineFormatter(lineCount: Int): ((String, Int)) => String = {
    val width = lineCount.toString.length
    def formatLine(lineWithIndex: (String, Int)): String = {
      val (line, index) = lineWithIndex
      val lineNumber = index + 1
      val numberAlignment = s"%${width}d"
      val alignedNumber = numberAlignment.format(lineNumber)
      s"($alignedNumber) $line"
    }
    formatLine
  }

  def breakLines(target: String): List[String] = target.split( """\r\n|\r|\n""", -1).toList

  trait LinesDifferenceResult {
    def isSame: Boolean

    def detailLines: Seq[String]
  }

  class Same(val detailLines: Seq[String]) extends LinesDifferenceResult {
    def isSame = true
  }

  class Difference(val detailLines: Seq[String]) extends LinesDifferenceResult {
    def isSame = false
  }

}
