package com.seanshubin.utility.collection

case class SortMe(id: Int, name: String){

  //Convenient alternative to: SortMe.unapply(_).get
  private def tupleIdName = (id, name)

  //Convenient alternative to: { val Some((a,b)) = SortMe.unapply(_); (b,a) }
  private def tupleNameId = (name, id)
}

object SortMe {
  val OrderingIdAscendingNameAscending: Ordering[SortMe] = new Ordering[SortMe] {
    override def compare(x: SortMe, y: SortMe): Int = {
      Ordering.Tuple2(Ordering.Int, Ordering.String).compare(x.tupleIdName, y.tupleIdName)
    }
  }
  val OrderingIdAscendingNameDescending: Ordering[SortMe] = new Ordering[SortMe] {
    override def compare(x: SortMe, y: SortMe): Int = {
      Ordering.Tuple2(Ordering.Int, Ordering.String.reverse).compare(x.tupleIdName, y.tupleIdName)
    }
  }
  val OrderingIdDescendingNameAscending: Ordering[SortMe] = new Ordering[SortMe] {
    override def compare(x: SortMe, y: SortMe): Int = {
      Ordering.Tuple2(Ordering.Int.reverse, Ordering.String).compare(x.tupleIdName, y.tupleIdName)
    }
  }
  val OrderingIdDescendingNameDescending: Ordering[SortMe] = new Ordering[SortMe] {
    override def compare(x: SortMe, y: SortMe): Int = {
      Ordering.Tuple2(Ordering.Int.reverse, Ordering.String.reverse).compare(x.tupleIdName, y.tupleIdName)
    }
  }
  val OrderingNameAscendingIdAscending: Ordering[SortMe] = new Ordering[SortMe] {
    override def compare(x: SortMe, y: SortMe): Int = {
      Ordering.Tuple2(Ordering.String, Ordering.Int).compare(x.tupleNameId, y.tupleNameId)
    }
  }
  val OrderingNameAscendingIdDescending: Ordering[SortMe] = new Ordering[SortMe] {
    override def compare(x: SortMe, y: SortMe): Int = {
      Ordering.Tuple2(Ordering.String, Ordering.Int.reverse).compare(x.tupleNameId, y.tupleNameId)
    }
  }
  val OrderingNameDescendingIdAscending: Ordering[SortMe] = new Ordering[SortMe] {
    override def compare(x: SortMe, y: SortMe): Int = {
      Ordering.Tuple2(Ordering.String.reverse, Ordering.Int).compare(x.tupleNameId, y.tupleNameId)
    }
  }
  val OrderingNameDescendingIdDescending: Ordering[SortMe] = new Ordering[SortMe] {
    override def compare(x: SortMe, y: SortMe): Int = {
      Ordering.Tuple2(Ordering.String.reverse, Ordering.Int.reverse).compare(x.tupleNameId, y.tupleNameId)
    }
  }
}
