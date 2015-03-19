package com.seanshubin.utility.filesystem

import java.io._
import java.nio.charset.Charset
import java.nio.file.{FileVisitor, Files, Path}

class FileSystemIntegrationImpl extends FileSystemIntegration {
  override def readAllBytes(path: Path): Array[Byte] = Files.readAllBytes(path)

  override def createDirectories(path: Path): Path = Files.createDirectories(path)

  override def walkFileTree(start: Path, visitor: FileVisitor[_ >: Path]): Path = Files.walkFileTree(start, visitor)

  override def write(path: Path, bytes: Array[Byte]): Path = Files.write(path, bytes)

  override def write(path: Path, lines: java.lang.Iterable[_ <: CharSequence], charset: Charset): Path =
    Files.write(path, lines, charset)

  override def readAllLines(path: Path, charset: Charset): java.util.List[String] = Files.readAllLines(path, charset)

  override def isDirectory(path: Path): Boolean = Files.isDirectory(path)

  override def deleteIfExists(path: Path): Boolean = Files.deleteIfExists(path)

  override def exists(path: Path): Boolean = Files.exists(path)

  override def newOutputStream(path: Path): OutputStream = Files.newOutputStream(path: Path): OutputStream

  override def newInputStream(path: Path): InputStream = Files.newInputStream(path)

  override def newBufferedWriter(path: Path, charset: Charset): BufferedWriter = Files.newBufferedWriter(path, charset)

  override def newBufferedReader(path: Path, charset: Charset): BufferedReader = Files.newBufferedReader(path, charset)
}
