package com.seanshubin.utility.filesystem

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._

import org.scalatest.FunSuite

import scala.collection.JavaConversions
import scala.collection.mutable.ArrayBuffer

class FileSystemIntegrationTest extends FunSuite {
  val fileSystems: Map[String, ((FileSystemIntegration, Path) => Unit) => Unit] = Map(
    "real" -> doWithRealFileSystem,
    "fake" -> doWithFakeFileSystem)
  val charset = StandardCharsets.UTF_8

  def doWithFakeFileSystem(f: (FileSystemIntegration, Path) => Unit): Unit = {
    val fileSystem = new FileSystemIntegrationFake
    val tempDir = Paths.get("seanshubin-utility", "test", "in-memory")
    f(fileSystem, tempDir)
  }

  def doWithRealFileSystem(f: (FileSystemIntegration, Path) => Unit): Unit = {
    val fileSystem = new FileSystemIntegrationImpl
    val tempDir = Files.createTempDirectory("seanshubin-utility-test-")
    f(fileSystem, tempDir)
    Files.walkFileTree(tempDir, DeleteVisitor)
  }

  testWithFileSystem("write and read") {
    (fileSystem, dir) =>
      val path = dir.resolve("hello.txt")
      val expected = "Hello, world!"
      fileSystem.write(path, expected.getBytes(charset))
      val actual = new String(fileSystem.readAllBytes(path).toArray, charset)
      assert(actual === expected)
  }

  testWithFileSystem("exists") {
    (fileSystem, dir) =>
      val path = dir.resolve("hello.txt")
      fileSystem.write(path, "Hello, world!".getBytes(charset))
      assert(fileSystem.exists(path))
  }

  testWithFileSystem("create directories") {
    (fileSystem, dir) =>
      val foo = dir.resolve("foo")
      val fooBar = foo.resolve("bar")
      fileSystem.createDirectories(fooBar)
      assert(fileSystem.isDirectory(fooBar))
      assert(fileSystem.isDirectory(foo))
  }

  testWithFileSystem("walk file tree") {
    (fileSystem, dir) =>
      val foo = dir.resolve("foo")
      val fooBar = foo.resolve("bar")
      fileSystem.createDirectories(fooBar)
      val path = fooBar.resolve("hello.txt")
      fileSystem.write(path, "Hello, world!".getBytes(charset))
      val visitor = new TestFileVisitor
      fileSystem.walkFileTree(foo, visitor)
      assert(visitor.events.size === 5)
      assert(visitor.events(0) === PreVisitDirectory(foo))
      assert(visitor.events(1) === PreVisitDirectory(fooBar))
      assert(visitor.events(2) === VisitFile(path))
      assert(visitor.events(3) === PostVisitDirectory(fooBar))
      assert(visitor.events(4) === PostVisitDirectory(foo))
  }

  testWithFileSystem("is directory") {
    (fileSystem, dir) =>
      val path = dir.resolve("hello.txt")
      fileSystem.write(path, "Hello, world!".getBytes(charset))
      assert(fileSystem.isDirectory(dir))
      assert(!fileSystem.isDirectory(path))
  }

  testWithFileSystem("delete if exists") {
    (fileSystem, dir) =>
      val path = dir.resolve("hello.txt")
      assert(!fileSystem.exists(path))
      fileSystem.deleteIfExists(path)
      assert(!fileSystem.exists(path))
      fileSystem.write(path, "Hello, world!".getBytes(charset))
      assert(fileSystem.exists(path))
      fileSystem.deleteIfExists(path)
      assert(!fileSystem.exists(path))
  }

  testWithFileSystem("streaming binary io") {
    (fileSystem, dir) =>
      val path = dir.resolve("hello.txt")
      val expected = "Hello, world!"
      val bytes = expected.getBytes(charset)
      val out = fileSystem.newOutputStream(path)
      out.write(bytes)
      out.close()
      assert(fileSystem.exists(path))
      val in = fileSystem.newInputStream(path)
      val inBytes: Array[Byte] = new Array(bytes.size)
      in.read(inBytes)
      assert(in.read() == -1)
      in.close()
      val actual = new String(inBytes, charset)
      assert(actual === expected)
  }

  testWithFileSystem("streaming text io") {
    (fileSystem, dir) =>
      val path = dir.resolve("hello.txt")
      val expected = "Hello, world!"
      val out = fileSystem.newBufferedWriter(path, charset)
      out.write(expected)
      out.close()
      assert(fileSystem.exists(path))
      val in = fileSystem.newBufferedReader(path, charset)
      val inChars: Array[Char] = new Array(expected.length)
      in.read(inChars)
      assert(in.read() == -1)
      in.close()
      val actual = new String(inChars)
      assert(actual === expected)
  }

  testWithFileSystem("write and read lines") {
    (fileSystem, dir) =>
      val path = dir.resolve("hello.txt")
      val expected = Seq("Hello, line one!", "Hello, line two!")
      val charset = StandardCharsets.UTF_8
      fileSystem.write(path, JavaConversions.asJavaIterable(expected), charset)
      assert(fileSystem.exists(path))
      val actual = JavaConversions.collectionAsScalaIterable(fileSystem.readAllLines(path, charset))
      assert(actual.toSeq === expected.toSeq)
  }

  sealed trait FileVisitorEvent

  case class PreVisitDirectory(dir: Path) extends FileVisitorEvent

  case class VisitFileFailed(file: Path) extends FileVisitorEvent

  case class VisitFile(file: Path) extends FileVisitorEvent

  case class PostVisitDirectory(dir: Path) extends FileVisitorEvent

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

  private def testWithFileSystem(testName: String)(doWithFileSystem: (FileSystemIntegration, Path) => Unit): Unit = {
    for {
      (fileSystemName, createFileSystem) <- fileSystems
    } {
      test(s"$fileSystemName - $testName")(createFileSystem(doWithFileSystem))
    }
  }
}
