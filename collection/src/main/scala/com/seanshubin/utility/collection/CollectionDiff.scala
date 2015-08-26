package com.seanshubin.utility.collection

import scala.annotation.tailrec

object CollectionDiff {
  def compare[T](left:Iterable[T], right:Iterable[T]):Result[T] = {
    val empty = Result[T](Seq(), Seq())
    val result = empty //compareRecursive(empty, left.toList, right.toList)
    result
  }

//  @tailrec
//  def compareRecursive[T](result:Result[T], leftRemain:List[T], rightRemain:List[T]): Result[T] = {
//    (leftRemain, rightRemain) match {
//      case (leftHead :: leftTail, rightHead :: rightTail) =>
//        if(leftHead == rightHead) {
//          compareRecursive(result, leftTail, rightTail)
//        } else {
//
//        }
//      case (Nil, rightHead :: rightTail) =>
//        result.copy(added = result.added ++ rightTail)
//      case (leftHead :: leftTail, Nil) =>
//        result.copy(added = result.removed ++ leftTail)
//      case (Nil, Nil) =>
//        result
//    }
//  }

  case class ResultBuilder[T](result:Result[T], trackingLeft:List[T], trackingRight:List[T])

  case class Result[T](added:Seq[T], removed:Seq[T]) {
    def isSame = added.isEmpty && removed.isEmpty
  }
}
