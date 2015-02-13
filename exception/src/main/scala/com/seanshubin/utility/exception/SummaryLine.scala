package com.seanshubin.utility.exception

case class SummaryLine(quantity: Int,
                       message: String,
                       declaringClass: String,
                       methodName: String,
                       fileName: String,
                       lineNumber: Int) extends Ordered[SummaryLine] {
  override def compare(that: SummaryLine): Int =
    if (this.quantity != that.quantity) that.quantity compare this.quantity
    else if (this.fileName != that.fileName) this.fileName compare that.fileName
    else if (this.lineNumber != that.lineNumber) this.lineNumber compare that.lineNumber
    else if (this.declaringClass != that.declaringClass) this.declaringClass compare that.declaringClass
    else if (this.methodName != that.methodName) this.methodName compare that.methodName
    else if (this.message != that.message) this.message compare that.message
    else 0

  override def toString: String = {
    val formattedDeclaringClass = removeAfterChar(declaringClass, '$')
    val formattedMethodName = removeAfterChar(methodName, '$')
    s"($quantity times): '$message' $formattedDeclaringClass.$formattedMethodName [$fileName:$lineNumber]"
  }

  private def removeAfterChar(x: String, c: Char): String = {
    val index = x.indexOf(c)
    val formatted = if (index == -1) x else x.substring(0, index)
    formatted
  }
}
