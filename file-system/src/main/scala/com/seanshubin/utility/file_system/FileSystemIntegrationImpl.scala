package com.seanshubin.utility.file_system

import java.nio.file.{FileVisitor, Files, Path}

class FileSystemIntegrationImpl extends FileSystemIntegration {
  override def readAllBytes(path: Path): Array[Byte] = Files.readAllBytes(path)

  override def createDirectories(path: Path): Path = Files.createDirectories(path)

  override def walkFileTree(start: Path, visitor: FileVisitor[_ >: Path]): Path = Files.walkFileTree(start, visitor)

  override def write(path: Path, bytes: Array[Byte]): Path = Files.write(path, bytes)

  override def isDirectory(path: Path): Boolean = Files.isDirectory(path)

  override def deleteIfExists(path: Path): Boolean = Files.deleteIfExists(path)

  override def exists(path: Path): Boolean = Files.exists(path)
}
