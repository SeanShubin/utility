package com.seanshubin.utility.http

import java.util
import java.util.Collections

import org.scalatest.FunSuite

class HeadersTest extends FunSuite {
  test("effective charset") {
    val javaHeaders: util.Map[String, util.List[String]] = new util.HashMap[String, util.List[String]]()
    javaHeaders.put("Access-Control-Allow-Origin", Collections.singletonList("*"))
    javaHeaders.put("Transfer-Encoding", Collections.singletonList("chunked"))
    javaHeaders.put("Server", Collections.singletonList("Oracle-HTTP-Server"))
    javaHeaders.put("X-Frame-Options", Collections.singletonList("SAMEORIGIN"))
    javaHeaders.put("Connection", Collections.singletonList("Transfer-Encoding, keep-alive"))
    javaHeaders.put("X-ORACLE-DMS-ECID", Collections.singletonList("005IGtgW3NQ9Pdw70Fr2EF0001E_00MACQ"))
    javaHeaders.put("Actual-Object-TTL", Collections.singletonList("1800"))
    javaHeaders.put("Content-Type", Collections.singletonList("text/html; charset=utf-8"))
    javaHeaders.put("Date", Collections.singletonList("Tue, 21 Feb 2017 18:40:06 GMT"))
    javaHeaders.put("Surrogate-Control", Collections.singletonList("content=\"ESI/1.0\""))
    javaHeaders.put("X-ORACLE-DMS-RID", Collections.singletonList("0:4"))
    val headers = Headers.fromJava(javaHeaders)
    val charsetName = headers.effectiveCharsetName
    assert(charsetName === "utf-8")
  }
}
