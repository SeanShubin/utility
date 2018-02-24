package com.seanshubin.utility.exec

import java.io.{ByteArrayInputStream, File, InputStream, OutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.time.{Clock, Instant, ZoneId}
import java.util
import java.util.concurrent.TimeUnit

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ProcessLauncherTest extends FunSuite {
  val charset = StandardCharsets.UTF_8

  test("launch process") {
    // given
    implicit val executionContext = new ExecutionContextStub
    val inputText =
      """input
        |text
        |lines""".stripMargin
    val errorText =
      """error
        |text
        |lines""".stripMargin
    val exitCode = 123
    val processStub = new ProcessStub(inputText, errorText, exitCode)
    val processBuilderStub = new ProcessBuilderStub(processStub)
    val createBuilderStub = new CreateBuilderStub(processBuilderStub)
    val futureRunnerStub = new FutureRunnerStub
    val started = Instant.ofEpochMilli(12345)
    val ended = Instant.ofEpochMilli(23456)
    val clockStub = new ClockStub(started, ended)
    val launcher = new ProcessLauncherImpl(createBuilderStub, futureRunnerStub, clockStub, charset)
    val command = Seq("some", "command")
    val directory = Paths.get("some/directory")
    val environment = Map("some" -> "environment")
    val input = ProcessInput(command, directory, environment)
    val expected = ProcessOutput(
      exitCode = 123,
      outputLines = Seq("input", "text", "lines"),
      errorLines = Seq("error", "text", "lines"),
      started,
      ended)

    // when
    val futureProcessOutput = launcher.launch(input)

    // then
    verifyFuture(futureProcessOutput, expected)
    assert(processBuilderStub.commandInvocations === Seq(Seq("some", "command")))
    assert(processBuilderStub.directoryInvocations === Seq(new File("some/directory")))
    assert(processBuilderStub.javaMap.size() === 1)
    assert(processBuilderStub.javaMap.get("some") === "environment")
  }

  def verifyFuture(future: Future[ProcessOutput], expected: ProcessOutput): Unit = {
    future.value match {
      case Some(Failure(ex)) =>
        ex.printStackTrace() // pull out the stack trace that got buried in the future, otherwise it is difficult to figure out why the test failed
        fail()
      case Some(Success(actual)) => assert(actual === expected)
      case None => fail("The execution context stub is synchronous, so the future should always be resolved by the time we get here")
    }
  }

  class CreateBuilderStub(processBuilderContract: ProcessBuilderContract) extends (() => ProcessBuilderContract) {
    override def apply(): ProcessBuilderContract = processBuilderContract
  }

  class FutureRunnerStub extends FutureRunner {
    override def runInFuture[T](block: => T): Future[T] = Future.successful(block)
  }

  class ClockStub(times: Instant*) extends Clock {
    var index = 0

    override def withZone(zone: ZoneId): Clock = ???

    override def getZone: ZoneId = ???

    override def instant(): Instant = {
      val result = times(index)
      index += 1
      result
    }
  }

  class ExecutionContextStub extends ExecutionContext {
    // we are not testing different orders of resolution, so just run it synchronously
    override def execute(runnable: Runnable): Unit = runnable.run()

    override def reportFailure(cause: Throwable): Unit = ???
  }

  class ProcessBuilderStub(process: ProcessContract) extends ProcessBuilderContract {
    val javaMap = new util.HashMap[String, String]()
    val commandInvocations = new ArrayBuffer[Seq[String]]
    val directoryInvocations = new ArrayBuffer[File]

    override def command(command: String*): ProcessBuilderContract = {
      commandInvocations.append(command)
      this
    }

    override def directory(directory: File): ProcessBuilderContract = {
      directoryInvocations.append(directory)
      this
    }

    override def environment: util.Map[String, String] = javaMap

    override def redirectOutput(redirectToMe: ProcessBuilder.Redirect): ProcessBuilderContract = ???

    override def redirectError(redirectToMe: ProcessBuilder.Redirect): ProcessBuilderContract = ???

    override def start: ProcessContract = process
  }

  class ProcessStub(inputText: String, errorText: String, exitCode: Int) extends ProcessContract {
    override def getOutputStream: OutputStream = ???

    override def getInputStream: InputStream = new InputStreamStub(inputText)

    override def getErrorStream: InputStream = new InputStreamStub(errorText)

    override def waitFor: Int = exitCode

    override def waitFor(timeout: Long, unit: TimeUnit): Boolean = ???

    override def exitValue: Int = ???

    override def destroy(): Unit = ???

    override def destroyForcibly: ProcessContract = ???

    override def isAlive: Boolean = ???
  }

  class InputStreamStub(text: String) extends InputStream {
    val bytes = text.getBytes(charset)
    val byteArrayInputStream = new ByteArrayInputStream(bytes)

    override def read(): Int = byteArrayInputStream.read()
  }

}
