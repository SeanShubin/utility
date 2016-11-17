package com.seanshubin.utility.http

import org.scalatest.FunSuite

class ContentTypeUtilTest extends FunSuite {
  //Content-Type: text/html; charset=ISO-8859-4
  ignore("get charset"){
    //given
    val headerValue = "text/html; charset=UTF-8"

    //when
    val contentType = ContentType.parse(headerValue)

    //then

    assert(contentType.theType === "text")
    assert(contentType.subtype === "html")
    assert(contentType.typeAndSubtype === "text/html")
    assert(contentType.parameters.size === 1)
    assert(contentType.parameters(0).name === "charset")
    assert(contentType.parameters(0).value === "UTF-8")
    assert(contentType.effectiveCharset === "UTF-8")

  }
  //type
  //subtype
  //charset
  //pull apart
  //piece together
}
