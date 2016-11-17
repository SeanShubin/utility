package com.seanshubin.utility.http

case class ContentType(theType:String, subtype:String, parameters:Seq[NameValue]){
  def typeAndSubtype:String = theType + "/" + subtype
  def effectiveCharset:String = ???
}

object ContentType {
  def parse(s:String):ContentType = {
    ???
  }
}
