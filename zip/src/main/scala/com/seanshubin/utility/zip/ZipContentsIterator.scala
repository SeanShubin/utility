package com.seanshubin.utility.zip

import java.io.InputStream
import java.util.zip.{ZipEntry, ZipInputStream}

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

class ZipContentsIterator(inputStream: InputStream, name: String, isZip: String => Boolean) extends Iterator[ZipContents] {

  private case class History(name: String, zipInputStream: ZipInputStream)

  private[this] var path: List[History] = History(name, new ZipInputStream(inputStream)) :: Nil
  private[this] var maybeNextEntry: Option[ZipEntry] = Option(latestZipInputStream.getNextEntry)

  def hasNext: Boolean = maybeNextEntry.isDefined

  def next(): ZipContents = {
    maybeNextEntry match {
      case Some(nextEntry) =>
        val bytes = loadBytes(nextEntry)
        val result = new ZipContents(pathNames, nextEntry, bytes)
        moveCursorForward()
        result
      case None => throw new RuntimeException("End of iterator")
    }
  }

  def closeBackingInputStreamEarly() {
    inputStream.close()
  }

  private def loadBytes(zipEntry: ZipEntry): Array[Byte] = {
    if (zipEntry.getSize == -1) {
      //sometimes this api tells me the size is -1 even though there are bytes to be read
      val arrayBuffer = new ArrayBuffer[Byte]()
      @tailrec
      def readRemainingBytes(): Unit = {
        val theByte = latestZipInputStream.read()
        if (theByte != -1) {
          arrayBuffer.append(theByte.asInstanceOf[Byte])
          readRemainingBytes()
        }
      }
      readRemainingBytes()
      arrayBuffer.toArray
    } else {
      val byteArray = Array.ofDim[Byte](zipEntry.getSize.toInt)
      //workaround
      //I tried doing latestZipInputStream.read(byteArray)
      //but it only populated the first 176 bytes
      //the rest were still zero
      //when I read each individual byte, it works fine
      for {
        index <- 0 until zipEntry.getSize.toInt
      } {
        val byte = latestZipInputStream.read()
        byteArray(index) = byte.asInstanceOf[Byte]
      }
      byteArray
    }
  }

  private def latestZipInputStream = path.head.zipInputStream

  private def extractName(history: History) = history.name

  private def pathNames = path.map(extractName).reverse

  private def moveCursorForward() {
    if (!hasNext) throw new RuntimeException("Can't move past end of iterator")
    val entry = latestZipInputStream.getNextEntry
    if (entry == null) {
      path = path.tail
      if (path == Nil) {
        maybeNextEntry = None
        inputStream.close()
      } else {
        moveCursorForward()
      }
    } else {
      if (entry.isDirectory) {
        maybeNextEntry = Some(entry)
      } else if (isZip(entry.getName)) {
        val zipInputStream = new ZipInputStream(latestZipInputStream)
        path = History(entry.getName, zipInputStream) :: path
        moveCursorForward()
      } else {
        maybeNextEntry = Some(entry)
      }
    }
  }
}
