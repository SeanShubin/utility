package com.seanshubin.utility.exception

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class ExceptionTrackerTest extends FunSuite {
  test("track exceptions") {
    val exceptions = new ArrayBuffer[Throwable]
    for (i <- 1 to 4) {
      exceptions.append(new RuntimeException(s"foo $i"))
    }
    for (i <- 1 to 2) {
      exceptions.append(new RuntimeException(s"bar $i"))
    }
    def updateExceptionTracker(exceptionTracker: ExceptionTracker, exception: Throwable):ExceptionTracker = {
      exceptionTracker.addException(exception)
    }
    val exceptionTracker = exceptions.foldLeft(ExceptionTracker.Empty)(updateExceptionTracker)
    assert(exceptionTracker.total === 6)
    assert(exceptionTracker.unique === 2)
    val summary = exceptionTracker.summary
    assert(summary.size === 2)
    assert(summary(0) === "(4 times): 'foo 1' com.seanshubin.utility.exception.ExceptionTrackerTest.apply [ExceptionTrackerTest.scala:11]")
    assert(summary(1) === "(2 times): 'bar 1' com.seanshubin.utility.exception.ExceptionTrackerTest.apply [ExceptionTrackerTest.scala:14]")
  }
}
