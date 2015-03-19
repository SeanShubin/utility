package com.seanshubin.utility.filesystem

import java.io.{BufferedReader, BufferedWriter, InputStream, OutputStream}
import java.lang.Iterable
import java.nio.charset.Charset
import java.nio.file.{FileVisitor, Path}
import java.util

class FileSystemIntegrationNotImplemented extends FileSystemIntegration {
  override def readAllBytes(path: Path): Array[Byte] = ???

  override def newOutputStream(path: Path): OutputStream = ???

  override def createDirectories(path: Path): Path = ???

  override def walkFileTree(start: Path, visitor: FileVisitor[_ >: Path]): Path = ???

  override def newInputStream(path: Path): InputStream = ???

  override def write(path: Path, bytes: Array[Byte]): Path = ???

  override def write(path: Path, lines: Iterable[_ <: CharSequence], charset: Charset): Path = ???

  override def isDirectory(path: Path): Boolean = ???

  override def readAllLines(path: Path, charset: Charset): util.List[String] = ???

  override def deleteIfExists(path: Path): Boolean = ???

  override def exists(path: Path): Boolean = ???

  override def newBufferedWriter(path: Path, charset: Charset): BufferedWriter = ???

  override def newBufferedReader(path: Path, charset: Charset): BufferedReader = ???
}
