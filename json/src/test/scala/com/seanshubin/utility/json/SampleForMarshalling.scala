package com.seanshubin.utility.json

case class SampleForMarshalling(stringSeq: Seq[String],
                                stringSeqSeq: Seq[Seq[String]],
                                optionString: Option[String])
