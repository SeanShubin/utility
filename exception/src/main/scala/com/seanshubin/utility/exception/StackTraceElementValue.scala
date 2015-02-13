package com.seanshubin.utility.exception

case class StackTraceElementValue(declaringClass: String, methodName: String, fileName: String, lineNumber: Int) {
  def this(that: StackTraceElement) = this(that.getClassName, that.getMethodName, that.getFileName, that.getLineNumber)

  override def toString: String = {
    val formattedDeclaringClass = removeAfterChar(declaringClass, '$')
    val formattedMethodName = removeAfterChar(methodName, '$')
    s"$formattedDeclaringClass.$formattedMethodName [$fileName:$lineNumber]"
  }

  private def removeAfterChar(x: String, c: Char): String = {
    val index = x.indexOf(c)
    val formatted = if (index == -1) x else x.substring(0, index)
    formatted
  }
}
