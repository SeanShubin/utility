package com.seanshubin.utility.json

import java.time._
import java.time.temporal.ChronoUnit

import org.scalatest.FunSuite

class JsonMarshallerTest extends FunSuite {
  val jsonMarshaller: JsonMarshaller = JacksonJsonMarshaller
  test("sample to json") {
    val sample = SampleForMarshalling(Seq("aaa"), Seq(Seq("bbb")), Some("ccc"))
    val actual = jsonMarshaller.toJson(sample)
    val expected = normalize( """{ "stringSeq" : [ "aaa" ],  "stringSeqSeq" : [ [ "bbb" ] ],  "optionString" : "ccc"}""")
    assert(actual === expected)
  }
  test("sample from json") {
    val json = """{ "stringSeq" : [ "aaa" ], "stringSeqSeq" : [ [ "bbb" ] ], "optionString" : "ccc"}"""
    val expected = SampleForMarshalling(Seq("aaa"), Seq(Seq("bbb")), Some("ccc"))
    val actual = jsonMarshaller.fromJson(json, classOf[SampleForMarshalling])
    assert(actual === expected)
  }
  test("normalize") {
    val a = normalize( """{"a":[1,2,3],"b":4,"c":{"d":"e"}}""")
    val b = normalize(
      """{
        |  "a" : [1,2,3],
        |  "b" : 4,
        |  "c" :
        |  {
        |    "d" : "e"
        |  }
        |}""".stripMargin)
    assert(a === b)
  }
  test("sensible error message on failure to parse") {
    val jsonMissingClosingBrace = """{ "a" : "b" """
    try {
      jsonMarshaller.fromJson(jsonMissingClosingBrace, classOf[Map[String, String]])
      fail("should have thrown exception")
    } catch {
      case ex: RuntimeException =>
        val expectedMessage =
          "Error while attempting to parse \"{ \\\"a\\\" : \\\"b\\\" \": " +
            "Unexpected end-of-input: expected close marker for Object " +
            "(start marker at [Source: { \"a\" : \"b\" ; line: 1, column: 1])\n" +
            " at [Source: { \"a\" : \"b\" ; line: 1, column: 25]"
        val actualMessage = ex.getMessage
        assert(actualMessage === expectedMessage)
    }
  }
  test("ignore unknown properties") {
    val json = """{ "bar": 123, "baz":456 }"""
    val expected = UnknownPropertiesTestHelper(123)
    val actual = jsonMarshaller.fromJson(json, classOf[UnknownPropertiesTestHelper])
    assert(actual === expected)
  }
  test("don't serialize null or empty properties") {
    val theObject = NullPropertiesTestHelper("aaa", null, Some("ccc"), None)
    val expected = jsonMarshaller.normalize( """{ "a": "aaa", "c":"ccc" }""")
    val actual = jsonMarshaller.toJson(theObject)
    assert(actual === expected)
  }
  test("make json pretty") {
    val actual = normalize( """{"a":"b","c":"d"}""")
    val expected =
      """{
        |  "a" : "b",
        |  "c" : "d"
        |}""".stripMargin
    assert(actual === expected)
  }
  test("array from json") {
    val json = """[ 1, 2, 3 ]"""
    val expected = Seq(1, 2, 3)
    val actual = jsonMarshaller.fromJsonArray(json, classOf[Int])
    assert(actual === expected)
  }
  test("merge map into map") {
    assert(merge( """{"a":1, "b":2, "c":3}""", """{"b":3, "c":null, "d":4}""") === normalize( """{"a":1, "b":3, "d":4}"""))
  }
  test("java 8 time types") {
    val epochMilli = 1445550663281L
    val zoneId = ZoneId.of("America/Los_Angeles")
    val instant = Instant.ofEpochMilli(epochMilli)
    val theObject = SampleTimeTypes(
      instant = instant,
      duration = Duration.of(5, ChronoUnit.MINUTES),
      localDateTime = LocalDateTime.ofInstant(instant, zoneId),
      localDate = LocalDate.of(2008, 12, 21),
      localTime = LocalTime.of(13, 30, 25),
      zoneId = zoneId,
      zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
    )
    val jsonText = jsonMarshaller.toJson(theObject)
    val expected = """{
                     |  "instant" : "2015-10-22T21:51:03.281Z",
                     |  "duration" : "PT5M",
                     |  "localDateTime" : "2015-10-22T14:51:03.281",
                     |  "localDate" : "2008-12-21",
                     |  "localTime" : "13:30:25",
                     |  "zoneId" : "America/Los_Angeles",
                     |  "zonedDateTime" : "2015-10-22T14:51:03.281-07:00"
                     |}""".stripMargin
    assert(jsonText === expected)
  }

  def merge(a: String, b: String): String = {
    val aObject = jsonMarshaller.fromJson(a, classOf[AnyRef])
    val bObject = jsonMarshaller.fromJson(b, classOf[AnyRef])
    val cObject = jsonMarshaller.merge(aObject, bObject)
    val merged = jsonMarshaller.toJson(cObject)
    merged
  }

  def normalize(s: String) = jsonMarshaller.normalize(s)
}
