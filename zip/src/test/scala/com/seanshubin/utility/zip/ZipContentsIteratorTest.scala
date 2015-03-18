package com.seanshubin.utility.zip

import java.nio.charset.StandardCharsets

import org.scalatest.FunSuite

class ZipContentsIteratorTest extends FunSuite {
  test("iterator") {
    val expected =
      """sample-data.zip/file-a.txt
        |  Hello A!
        |sample-data.zip/file-b.txt
        |  Hello B!
        |sample-data.zip/zip-a.zip/dir-a/
        |sample-data.zip/zip-a.zip/dir-a/file-c.txt
        |  Hello C!
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/file-d.txt
        |  Hello D!
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/file-e.txt
        |  Hello E!
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-c.zip/dir-c/
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-c.zip/dir-c/file-f.txt
        |  Hello F!
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-c.zip/dir-c/file-g.txt
        |  Hello G!
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-d.zip/dir-d/
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-d.zip/dir-d/file-h.txt
        |  Hello H!
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-e.zip/dir-e/
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-e.zip/dir-e/file-i.txt
        |  Hello I!
        |sample-data.zip/zip-a.zip/dir-a/zip-b.zip/dir-b/zip-e.zip/dir-e/file-j.txt
        |  Hello J!
        |sample-data.zip/zip-a.zip/dir-a/zip-f.zip/dir-f/
        |sample-data.zip/zip-a.zip/dir-a/zip-f.zip/dir-f/file-k.txt
        |  Hello K!
        |sample-data.zip/zip-g.zip/dir-g/
        | """.trim.stripMargin.split("\n")
    def isZip(name: String) = name.endsWith(".zip")
    def operateOnCursor(cursor: ZipContents): Seq[String] = {
      val ZipContents(path, zipEntry, bytes) = cursor
      val pathString = (path :+ zipEntry.getName).mkString("/")
      if (zipEntry.isDirectory) {
        Seq(pathString)
      } else {
        val content = new String(bytes, StandardCharsets.UTF_8)
        Seq(pathString, "  " + content)
      }
    }
    val classLoader = this.getClass.getClassLoader
    val fileName = "sample-data.zip"
    val inputStream = classLoader.getResourceAsStream(fileName)
    val iterator = new ZipContentsIterator(inputStream, fileName, isZip)
    val actual = iterator.flatMap(operateOnCursor).toIndexedSeq
    val seqCompare = SeqCompare.compare(expected, actual)
    assert(seqCompare.isSame === true, seqCompare.message.mkString("\n"))
  }
}
