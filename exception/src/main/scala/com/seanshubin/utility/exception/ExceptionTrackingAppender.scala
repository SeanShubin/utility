package com.seanshubin.utility.exception

import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.{Filter, Layout, LogEvent}

class ExceptionTrackingAppender(name: String, filter: Filter, layout: Layout[_ <: Serializable], exceptionTracker: MutableExceptionTracker)
  extends AbstractAppender(name, filter, layout) {
  override def append(event: LogEvent): Unit = {
    val exception = event.getThrown
    if (exception != null) exceptionTracker.addException(exception)
  }
}
