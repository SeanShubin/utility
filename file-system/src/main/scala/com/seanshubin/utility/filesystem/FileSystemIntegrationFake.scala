package com.seanshubin.utility.filesystem

import java.io._
import java.nio.charset.Charset
import java.nio.file.{FileVisitor, Path, Paths}
import java.util

import scala.annotation.tailrec
import scala.collection.JavaConversions
import scala.collection.immutable.ListMap
import scala.collection.mutable.ArrayBuffer

class FileSystemIntegrationFake extends FileSystemIntegration {
  private var roots: Branches = Branches.Empty
  private var content: Map[Path, ArrayBuffer[Byte]] = ListMap()

  def toMultipleLineString: Seq[String] = {
    roots.toMultipleLineString ++ content.toList.map(contentToString)
  }

  private def contentToString(entry: (Path, Seq[Byte])): String = {
    val (path, bytes) = entry
    s"(${bytes.size} bytes) ${pathToPathParts(path).mkString("/")}"
  }

  class FakeOutputStream(path: Path) extends OutputStream {
    val buffer = content(path)

    override def write(b: Int): Unit = buffer.append(b.toByte)
  }

  override def readAllBytes(path: Path): Seq[Byte] = content(path)

  override def newOutputStream(path: Path): OutputStream = {
    write(path, Seq())
    new FakeOutputStream(path)
  }

  override def createDirectories(path: Path): Path = {
    addFile(path)
    path
  }

  override def walkFileTree(start: Path, visitor: FileVisitor[_ >: Path]): Path = {
    val basePathParts = pathToPathParts(start)
    val tree = roots.treeAt(basePathParts)
    tree.traverse(new TreeVisitor {
      private var pathParts: List[String] = basePathParts.reverse.tail

      private def path: Path = Paths.get(pathParts.reverse.head, pathParts.reverse.tail: _*)

      override def before(name: String): Unit = {
        pathParts = name :: pathParts
        if (isDirectory(path)) {
          visitor.preVisitDirectory(path, null)
        } else {
          visitor.visitFile(path, null)
        }
      }

      override def after(name: String): Unit = {
        if (isDirectory(path)) {
          visitor.postVisitDirectory(path, null)
        }
        pathParts = pathParts.tail
      }
    })
    start
  }

  override def newInputStream(path: Path): InputStream = new ByteArrayInputStream(content(path).toArray)

  override def write(path: Path, bytes: Seq[Byte]): Path = {
    addFile(path)
    val buffer = new ArrayBuffer[Byte](bytes.size)
    buffer.appendAll(bytes)
    content += (path -> buffer)
    path
  }

  override def write(path: Path, javaLines: java.lang.Iterable[_ <: CharSequence], charset: Charset): Path = {
    val writer = new PrintWriter(newBufferedWriter(path, charset))
    val scalaLines = JavaConversions.iterableAsScalaIterable(javaLines)
    for {
      line <- scalaLines
    } {
      writer.println(line)
    }
    writer.flush()
    path
  }

  override def isDirectory(path: Path): Boolean = {
    val pathExists = exists(path)
    val containsContent = content.contains(path)
    val pathIsDirectory = pathExists && !containsContent
    pathIsDirectory
  }

  override def readAllLines(path: Path, charset: Charset): java.util.List[String] = {
    val reader = newBufferedReader(path, charset)
    val scalaLines = readerToLines(reader, Nil)
    val javaLines = new util.ArrayList[String]()
    for {
      line <- scalaLines
    } {
      javaLines.add(line)
    }
    javaLines
  }

  @tailrec
  private def readerToLines(reader: BufferedReader, soFar: List[String]): List[String] = {
    val line = reader.readLine()
    if (line == null) soFar.reverse
    else readerToLines(reader, line :: soFar)
  }

  override def deleteIfExists(path: Path): Boolean = {
    val alreadyExists = exists(path)
    if (alreadyExists) {
      content -= path
      roots = roots.remove(pathToPathParts(path))
    }
    alreadyExists
  }

  override def exists(path: Path): Boolean = {
    val pathParts = pathToPathParts(path)
    roots.pathExists(pathParts)
  }

  override def newBufferedWriter(path: Path, charset: Charset): BufferedWriter =
    new BufferedWriter(new OutputStreamWriter(newOutputStream(path), charset))

  override def newBufferedReader(path: Path, charset: Charset): BufferedReader =
    new BufferedReader(new InputStreamReader(newInputStream(path), charset))

  private def addFile(path: Path): Unit = {
    val pathParts = pathToPathParts(path)
    roots = roots.add(pathParts: _*)
  }

  private def pathToPathParts(path: Path): List[String] = {
    val pathParts = JavaConversions.asScalaIterator(path.iterator()).map(_.toString).toList
    val newHead = if (path.isAbsolute) {
      "/" + pathParts.head
    } else {
      pathParts.head
    }
    newHead :: pathParts.tail
  }
}
