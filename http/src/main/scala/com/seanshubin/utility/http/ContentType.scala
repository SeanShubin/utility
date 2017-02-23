package com.seanshubin.utility.http

class ContentType(typeAndSubtype: String, maybeCharset: Option[String]) {
  def effectiveCharsetName: String = maybeCharset.getOrElse(HttpConstants.DefaultCharset)
}

object ContentType {
  private val wordPattern = """[\w\-]+"""
  private val maybeSpacesPattern = """\s*"""
  private val contentTypeOnlyPattern = RegexUtil.capture(wordPattern + "/" + wordPattern)
  private val charsetPattern = "charset" + maybeSpacesPattern + "=" + maybeSpacesPattern + RegexUtil.capture(wordPattern)
  private val contentTypePattern =
    contentTypeOnlyPattern + maybeSpacesPattern + RegexUtil.optional(";" + maybeSpacesPattern + charsetPattern)
  private val ContentTypeRegex = contentTypePattern.r

  def fromHeaderValue(value: String): ContentType = {
    value match {
      case ContentTypeRegex(typeAndSubtype, possiblyNullCharset) =>
        new ContentType(typeAndSubtype, Option(possiblyNullCharset))
      case _ =>
        throw new RuntimeException(s"Value '$value' does not match pattern '$ContentTypeRegex' for content type")
    }
  }
}
