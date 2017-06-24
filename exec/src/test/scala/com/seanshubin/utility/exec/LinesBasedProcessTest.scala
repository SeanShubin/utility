package com.seanshubin.utility.exec

import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Path, Paths}

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class LinesBasedProcessTest extends FunSuite {
  test("lines based process") {
    val process: ProcessContract = new ProcessStub
    val processFactory: ProcessFactory = new ProcessFactoryStub(process)
    val command: Seq[String] = Seq("blah")
    val workingDirectory: Path = Paths.get(".")
    val environment: Map[String, String] = Map()
    val standardOutputLines: ArrayBuffer[String] = ArrayBuffer()
    val emitStandardOutputLine: String => Unit = line => standardOutputLines.append(line)
    val errorOutputLines: ArrayBuffer[String] = ArrayBuffer()
    val emitErrorOutputLine: String => Unit = line => errorOutputLines.append(line)
    val redirectErrorToStandard: Boolean = false
    val charset: Charset = StandardCharsets.UTF_8
    val executionContext: StubExecutionContext = new StubExecutionContext()
    val linesProcess = new LinesBasedProcessFromProcessFactory(
      command,
      workingDirectory,
      environment,
      emitStandardOutputLine,
      emitErrorOutputLine,
      redirectErrorToStandard,
      charset,
      processFactory,
      executionContext
    )
    assert(executionContext.history.size === 2)
    executionContext.runAll()
    assert(standardOutputLines === Seq("Target: "))
    linesProcess.sendLine("world")
    //    assert(process.currentStandardOutputLines === Seq("Target: ", "Hello, world!"))
  }
}
