package com.seanshubin.utility.filesystem

import java.io.{InputStream, OutputStream}
import java.nio.charset.Charset
import java.nio.file.{FileVisitor, Path}

trait FileSystemIntegration {
  def readAllBytes(path: Path): Array[Byte]

  def write(path: Path, bytes: Array[Byte]): Path

  def write(path: Path, lines: java.lang.Iterable[_ <: CharSequence], charset: Charset): Path

  def readAllLines(path: Path, charset: Charset): java.util.List[String]

  def isDirectory(path: Path): Boolean

  def walkFileTree(start: Path, visitor: FileVisitor[_ >: Path]): Path

  def createDirectories(path: Path): Path

  def exists(path: Path): Boolean

  def deleteIfExists(path: Path): Boolean

  def newOutputStream(path: Path): OutputStream

  def newInputStream(path: Path): InputStream
}
