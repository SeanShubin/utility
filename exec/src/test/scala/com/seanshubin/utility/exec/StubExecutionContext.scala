package com.seanshubin.utility.exec

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext

class StubExecutionContext extends ExecutionContext {
  val history: ArrayBuffer[Runnable] = ArrayBuffer()
  var hasRunIndices: Set[Int] = Set()

  def runAll(): Unit = {
    history.zipWithIndex.filterNot(hasRun.tupled).foreach(run.tupled)
  }

  val hasRun: (Runnable, Int) => Boolean = (runnable, index) => hasRunIndices.contains(index)
  val run: (Runnable, Int) => Unit = (runnable, index) => {
    hasRunIndices += index
    println(s"about to run $index")
    runnable.run()
    println(s"finished running $index")
  }

  override def execute(runnable: Runnable): Unit = {
    history.append(runnable)
  }

  override def reportFailure(cause: Throwable): Unit = {
    cause.printStackTrace()
    throw cause
  }
}
