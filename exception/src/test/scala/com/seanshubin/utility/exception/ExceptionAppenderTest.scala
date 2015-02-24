package com.seanshubin.utility.exception

import org.apache.logging.log4j.core.{Filter, Layout, Logger => LoggerImpl, LoggerContext}
import org.apache.logging.log4j.{LogManager, Logger}
import org.scalatest.FunSuite

class ExceptionAppenderTest extends FunSuite {
  test("track exceptions") {
    val name = "track-exceptions-appender"
    val mutableExceptionTracker: MutableExceptionTracker = new MutableExceptionTrackerImpl
    val filter: Filter = null
    val layout: Layout[_ <: Serializable] = null
    val exceptionTrackingAppender = new ExceptionTrackingAppender(name, filter, layout, mutableExceptionTracker)
    LogManager.getContext.asInstanceOf[LoggerContext].getConfiguration.addAppender(exceptionTrackingAppender)
    val rootLogger = LogManager.getRootLogger.asInstanceOf[LoggerImpl]
    rootLogger.addAppender(exceptionTrackingAppender)
    val log: Logger = LogManager.getLogger("track-exceptions")

    for (i <- 1 to 4) {
      log.error(s"foo message $i", new RuntimeException(s"foo $i"))
    }
    for (i <- 1 to 2) {
      log.error(s"bar message $i", new RuntimeException(s"bar $i"))
    }
    val exceptionTracker = mutableExceptionTracker.staleValue
    assert(exceptionTracker.total === 6)
    assert(exceptionTracker.unique === 2)
    val summary = exceptionTracker.summary
    assert(summary.size === 2)
    assert(summary(0) === "(4 times): 'foo 1' com.seanshubin.utility.exception.ExceptionAppenderTest.apply [ExceptionAppenderTest.scala:20]")
    assert(summary(1) === "(2 times): 'bar 1' com.seanshubin.utility.exception.ExceptionAppenderTest.apply [ExceptionAppenderTest.scala:23]")
  }
}
