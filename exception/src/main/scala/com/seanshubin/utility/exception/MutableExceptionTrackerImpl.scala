package com.seanshubin.utility.exception

import java.util.concurrent.atomic.AtomicReference

class MutableExceptionTrackerImpl extends MutableExceptionTracker {
  private val exceptionTrackerRef: AtomicReference[ExceptionTracker] = new AtomicReference(ExceptionTracker.Empty)

  override def addException(exception: Throwable): Unit = {
    var success = false
    while(!success) {
      val currentValue = exceptionTrackerRef.get()
      val newValue = currentValue.addException(exception)
      if(exceptionTrackerRef.compareAndSet(currentValue, newValue)) success = true
    }
  }

  override def staleValue: ExceptionTracker = exceptionTrackerRef.get()
}
