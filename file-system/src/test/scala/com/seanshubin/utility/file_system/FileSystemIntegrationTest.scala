package com.seanshubin.utility.file_system

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, FileVisitResult, FileVisitor, Path}
import java.nio.file.attribute.BasicFileAttributes

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class FileSystemIntegrationTest extends FunSuite {
  val fileSystem: FileSystemIntegration = new FileSystemIntegrationImpl
  val charset = StandardCharsets.UTF_8

  testWithTempDirectory("file io") {
    dir =>
      val path = dir.resolve("hello.txt")
      val expected = "Hello, world!"
      storeText(path, expected)
      val actual = loadText(path)
      assert(actual === expected)
  }

  testWithTempDirectory("exists") {
    dir =>
      val path = dir.resolve("hello.txt")
      val expected = "Hello, world!"
      storeText(path, expected)
      assert(fileSystem.exists(path))
  }

  testWithTempDirectory("create directories") {
    dir =>
      val foo = dir.resolve("foo")
      val fooBar = foo.resolve("bar")
      fileSystem.createDirectories(fooBar)
      assert(fileSystem.isDirectory(fooBar))
      assert(fileSystem.isDirectory(foo))
  }

  testWithTempDirectory("walk file tree") {
    dir =>
      val foo = dir.resolve("foo")
      val fooBar = foo.resolve("bar")
      fileSystem.createDirectories(fooBar)
      val path = fooBar.resolve("hello.txt")
      storeText(path, "Hello, world!")
      val visitor = new TestFileVisitor
      fileSystem.walkFileTree(foo, visitor)
      assert(visitor.events.size === 5)
      assert(visitor.events(0) === PreVisitDirectory(foo))
      assert(visitor.events(1) === PreVisitDirectory(fooBar))
      assert(visitor.events(2) === VisitFile(path))
      assert(visitor.events(3) === PostVisitDirectory(fooBar))
      assert(visitor.events(4) === PostVisitDirectory(foo))
      visitor.events.foreach(println )
  }

  private def storeText(path: Path, text: String): Unit = {
    val bytes = text.getBytes(charset)
    fileSystem.write(path, bytes)
  }

  private def loadText(path: Path): String = {
    val bytes = fileSystem.readAllBytes(path)
    val text = new String(bytes, charset)
    text
  }

  sealed trait FileVisitorEvent

  case class PreVisitDirectory(dir:Path) extends FileVisitorEvent
  case class VisitFileFailed(file:Path) extends FileVisitorEvent
  case class VisitFile(file:Path) extends FileVisitorEvent
  case class PostVisitDirectory(dir:Path) extends FileVisitorEvent


  private class TestFileVisitor extends FileVisitor[Path] {
    val events = new ArrayBuffer[FileVisitorEvent]()

    override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
      events.append(PreVisitDirectory(dir))
      FileVisitResult.CONTINUE
    }

    override def visitFileFailed(file: Path, exc: IOException): FileVisitResult = {
      events.append(VisitFileFailed(file))
      FileVisitResult.CONTINUE
    }

    override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
      events.append(VisitFile(file))
      FileVisitResult.CONTINUE
    }

    override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
      events.append(PostVisitDirectory(dir))
      FileVisitResult.CONTINUE
    }
  }

  private object DeleteVisitor extends FileVisitor[Path] {
    override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = FileVisitResult.CONTINUE

    override def visitFileFailed(file: Path, exc: IOException): FileVisitResult = FileVisitResult.CONTINUE

    override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
      Files.delete(file)
      FileVisitResult.CONTINUE
    }

    override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
      Files.delete(dir)
      FileVisitResult.CONTINUE
    }
  }

  private def withTempDirectory[T](doWithTempDirectory: Path => T): T = {
    val tempDir = Files.createTempDirectory("foo")
    val result = doWithTempDirectory(tempDir)
    Files.walkFileTree(tempDir, DeleteVisitor)
    result
  }

  private def testWithTempDirectory(testName: String)(doWithTempDirectory: Path => Unit): Unit = {
    test(testName)(withTempDirectory(doWithTempDirectory))
  }
}
