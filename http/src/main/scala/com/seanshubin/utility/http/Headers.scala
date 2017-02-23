package com.seanshubin.utility.http

import java.util

import scala.collection.JavaConverters._

class Headers(normalizedHeaders: Map[String, String]) {
  def effectiveCharsetName: String = {
    normalizedHeaders.get("content-type") match {
      case Some(contentTypeString) =>
        ContentType.fromHeaderValue(contentTypeString).effectiveCharsetName
      case None =>
        HttpConstants.DefaultCharset
    }
  }
}

object Headers {
  def fromJava(javaHeaders: util.Map[String, util.List[String]]): Headers = {
    val entryToScala: (String, util.List[String]) => Seq[(String, String)] = (key, values) => {
      values.asScala.map(value => (key, value))
    }
    val addToMap: (Map[String, String], (String, String)) => Map[String, String] = (map, entry) => {
      val (name, value) = entry
      map.get(name) match {
        case Some(existingValue) =>
          val newValue = existingValue + "," + value
          map + ((name, newValue))
        case None =>
          map + entry
      }
    }
    val nonNullHeader: (String, String) => Boolean = (key, _) => key != null
    val keyLowerCase: (String, String) => (String, String) = (key, value) => (key.toLowerCase, value)
    val emptyMap = Map[String, String]()
    val headers: Map[String, String] = javaHeaders.asScala.toSeq.flatMap(entryToScala.tupled).filter(nonNullHeader.tupled).map(keyLowerCase.tupled).foldLeft(emptyMap)(addToMap)
    new Headers(headers)
  }
}

/*
3.7.1 Canonicalization and Text Defaults

Internet media types are registered with a canonical form. An entity-body transferred via HTTP messages MUST be represented in the appropriate canonical form prior to its transmission except for "text" types, as defined in the next paragraph.

When in canonical form, media subtypes of the "text" type use CRLF as the text line break. HTTP relaxes this requirement and allows the transport of text media with plain CR or LF alone representing a line break when it is done consistently for an entire entity-body. HTTP applications MUST accept CRLF, bare CR, and bare LF as being representative of a line break in text media received via HTTP. In addition, if the text is represented in a character set that does not use octets 13 and 10 for CR and LF respectively, as is the case for some multi-byte character sets, HTTP allows the use of whatever octet sequences are defined by that character set to represent the equivalent of CR and LF for line breaks. This flexibility regarding line breaks applies only to text media in the entity-body; a bare CR or LF MUST NOT be substituted for CRLF within any of the HTTP control structures (such as header fields and multipart boundaries).

If an entity-body is encoded with a content-coding, the underlying data MUST be in a form defined above prior to being encoded.

The "charset" parameter is used with some media types to define the character set (section 3.4) of the data. When no explicit charset parameter is provided by the sender, media subtypes of the "text" type are defined to have a default charset value of "ISO-8859-1" when received via HTTP. Data in character sets other than "ISO-8859-1" or its subsets MUST be labeled with an appropriate charset value. See section 3.4.1 for compatibility problems.
 */

/*
4.2 Message Headers

HTTP header fields, which include general-header (section 4.5), request-header (section 5.3), response-header (section 6.2), and entity-header (section 7.1) fields, follow the same generic format as that given in Section 3.1 of RFC 822 [9]. Each header field consists of a name followed by a colon (":") and the field value. Field names are case-insensitive. The field value MAY be preceded by any amount of LWS, though a single SP is preferred. Header fields can be extended over multiple lines by preceding each extra line with at least one SP or HT. Applications ought to follow "common form", where one is known or indicated, when generating HTTP constructs, since there might exist some implementations that fail to accept anything

beyond the common forms.

       message-header = field-name ":" [ field-value ]
       field-name     = token
       field-value    = *( field-content | LWS )
       field-content  = <the OCTETs making up the field-value
                        and consisting of either *TEXT or combinations
                        of token, separators, and quoted-string>
The field-content does not include any leading or trailing LWS: linear white space occurring before the first non-whitespace character of the field-value or after the last non-whitespace character of the field-value. Such leading or trailing LWS MAY be removed without changing the semantics of the field value. Any LWS that occurs between field-content MAY be replaced with a single SP before interpreting the field value or forwarding the message downstream.

The order in which header fields with differing field names are received is not significant. However, it is "good practice" to send general-header fields first, followed by request-header or response- header fields, and ending with the entity-header fields.

Multiple message-header fields with the same field-name MAY be present in a message if and only if the entire field-value for that header field is defined as a comma-separated list [i.e., #(values)]. It MUST be possible to combine the multiple header fields into one "field-name: field-value" pair, without changing the semantics of the message, by appending each subsequent field-value to the first, each separated by a comma. The order in which header fields with the same field-name are received is therefore significant to the interpretation of the combined field value, and thus a proxy MUST NOT change the order of these field values when a message is forwarded.
*/