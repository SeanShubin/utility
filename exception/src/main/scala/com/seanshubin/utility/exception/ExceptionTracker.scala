package com.seanshubin.utility.exception

case class ExceptionTracker(exceptions: Map[Seq[StackTraceElementValue], QuantifiedException]) {
  def addException(exception: Throwable): ExceptionTracker = {
    def convertStackTrace(ex: Throwable): Seq[StackTraceElementValue] = {
      val stackTraceAsSeq = ex.getStackTrace.toSeq.map(element => new StackTraceElementValue(element))
      if (ex.getCause == null) {
        stackTraceAsSeq
      } else {
        stackTraceAsSeq ++ convertStackTrace(ex.getCause)
      }
    }
    val key = convertStackTrace(exception)
    val oldQuantified = exceptions.getOrElse(key, QuantifiedException(exception, 0))
    val newQuantified = oldQuantified.copy(quantity = oldQuantified.quantity + 1)
    val newExceptions = exceptions + (key -> newQuantified)
    ExceptionTracker(newExceptions)
  }

  def total: Int = exceptions.values.map(quantifiedException => quantifiedException.quantity).sum

  def unique: Int = exceptions.size

  def summary: Seq[String] = {
    val summaryLines = for {
      (key, value) <- exceptions
      quantity = value.quantity
      message = value.exception.getMessage
      declaringClass = key.head.declaringClass
      methodName = key.head.methodName
      fileName = key.head.fileName
      lineNumber = key.head.lineNumber
    } yield {
      SummaryLine(quantity, message, declaringClass, methodName, fileName, lineNumber)
    }
    summaryLines.toSeq.sorted.map(_.toString)
  }
}

object ExceptionTracker {
  val Empty = ExceptionTracker(Map.empty)
}
