package com.seanshubin.utility.exception

trait MutableExceptionTracker {
  def addException(exception: Throwable): Unit
  def staleValue:ExceptionTracker
}
