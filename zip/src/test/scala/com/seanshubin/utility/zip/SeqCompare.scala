package com.seanshubin.utility.zip

import scala.annotation.tailrec

trait SeqCompare {
  def isSame: Boolean

  def message: Seq[String]
}

object SeqCompare {
  def compare(actualSeq: Seq[String], expectedSeq: Seq[String]): SeqCompare = {
    compare(Nil, 0, actualSeq.toList, expectedSeq.toList)
  }

  @tailrec
  private def compare(reversedSoFar: List[String], index: Int, actualList: List[String], expectedList: List[String]): SeqCompare = {
    (actualList.headOption, expectedList.headOption) match {
      case (None, None) => SeqSame
      case (Some(actual), None) => SeqExtra(reversedSoFar.reverse, index, actual)
      case (None, Some(expected)) => SeqMissing(reversedSoFar.reverse, index, expected)
      case (Some(actual), Some(expected)) => if (actual == expected) {
        compare(expected :: reversedSoFar, index + 1, actualList.tail, expectedList.tail)
      } else {
        SeqDifference(reversedSoFar.reverse, index, actual, expected)
      }
    }
  }

}

case object SeqSame extends SeqCompare {
  override def isSame: Boolean = true

  override def message: Seq[String] = Seq()
}

case class SeqDifference(sameUntil: Seq[String],
                         differenceAtLine: Int,
                         actualLine: String,
                         expectedLine: String) extends SeqCompare {
  override def isSame: Boolean = false

  override def message: Seq[String] =
    Seq(s"difference at line $differenceAtLine") ++
      sameUntil ++
      Seq(
        s"actual  : '$actualLine'",
        s"expected: '$expectedLine'")
}

case class SeqMissing(sameUntil: Seq[String],
                      differenceAtLine: Int,
                      missingLine: String) extends SeqCompare {
  override def isSame: Boolean = false

  override def message: Seq[String] =
    Seq(s"missing at line $differenceAtLine") ++
      sameUntil ++
      Seq(
        s"missing: '$missingLine'")
}

case class SeqExtra(sameUntil: Seq[String],
                    differenceAtLine: Int,
                    extraLine: String) extends SeqCompare {
  override def isSame: Boolean = false

  override def message: Seq[String] =
    Seq(s"extra at line $differenceAtLine") ++
      sameUntil ++
      Seq(
        s"extra: '$extraLine'")
}
