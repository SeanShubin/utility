package com.seanshubin.utility.filesystem

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import org.scalatest.FunSuite

class FileSystemIntegrationFakeMultipleLineString extends FunSuite {
  val charset = StandardCharsets.UTF_8
  test("lots of content") {
    val expected =
      """file-1
        |aaa
        |  file-2
        |  ddd
        |  eee
        |  fff
        |bbb
        |  ggg
        |  hhh
        |    file-3
        |    file-4
        |    file-5
        |  iii
        |ccc
        |  jjj
        |  kkk
        |  lll
        |(14 bytes) file-1
        |(14 bytes) aaa/file-2
        |(14 bytes) bbb/hhh/file-3
        |(14 bytes) bbb/hhh/file-4
        |(14 bytes) bbb/hhh/file-5""".stripMargin.split("\r\n|\r|\n").mkString("\n")
    val fileSystem = new FileSystemIntegrationFake
    fileSystem.write(Paths.get("file-1"), "file 1 content".getBytes(charset))
    fileSystem.createDirectories(Paths.get("aaa"))
    fileSystem.write(Paths.get("aaa", "file-2"), "file 2 content".getBytes(charset))
    fileSystem.createDirectories(Paths.get("aaa", "ddd"))
    fileSystem.createDirectories(Paths.get("aaa", "eee"))
    fileSystem.createDirectories(Paths.get("aaa", "fff"))
    fileSystem.createDirectories(Paths.get("bbb"))
    fileSystem.createDirectories(Paths.get("bbb", "ggg"))
    fileSystem.createDirectories(Paths.get("bbb", "hhh"))
    fileSystem.write(Paths.get("bbb", "hhh", "file-3"), "file 3 content".getBytes(charset))
    fileSystem.write(Paths.get("bbb", "hhh", "file-4"), "file 4 content".getBytes(charset))
    fileSystem.write(Paths.get("bbb", "hhh", "file-5"), "file 5 content".getBytes(charset))
    fileSystem.createDirectories(Paths.get("bbb", "iii"))
    fileSystem.createDirectories(Paths.get("ccc"))
    fileSystem.createDirectories(Paths.get("ccc", "jjj"))
    fileSystem.createDirectories(Paths.get("ccc", "kkk"))
    fileSystem.createDirectories(Paths.get("ccc", "lll"))
    val actual = fileSystem.toMultipleLineString.mkString("\n")
    assert(actual === expected)
  }
}
