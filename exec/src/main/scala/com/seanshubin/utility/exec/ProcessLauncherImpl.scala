package com.seanshubin.utility.exec

import java.io._
import java.nio.charset.Charset
import java.time.Clock

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

class ProcessLauncherImpl(createProcessBuilder: () => ProcessBuilderContract,
                          futureRunner: FutureRunner,
                          clock: Clock,
                          charset: Charset)(implicit executionContext: ExecutionContext) extends ProcessLauncher {
  override def launch(input: ProcessInput): Future[ProcessOutput] = {
    launch(input, None, None)
  }

  override def launch(input: ProcessInput, redirectBoth: ProcessBuilder.Redirect): Future[ProcessOutput] = {
    launch(input, Some(redirectBoth), Some(redirectBoth))
  }

  override def launch(input: ProcessInput, redirectOutput: ProcessBuilder.Redirect, redirectError: ProcessBuilder.Redirect): Future[ProcessOutput] = {
    launch(input, Some(redirectOutput), Some(redirectError))
  }

  private def launch(input: ProcessInput,
                     maybeRedirectOutput: Option[ProcessBuilder.Redirect],
                     maybeRedirectError: Option[ProcessBuilder.Redirect]): Future[ProcessOutput] = {
    val processBuilder = createProcessBuilder()
    updateEnvironment(processBuilder, input.environment)
    val started = clock.instant()
    processBuilder.command(input.command: _*)
    processBuilder.directory(input.directory.toFile)
    maybeRedirectOutput.map(processBuilder.redirectOutput)
    maybeRedirectError.map(processBuilder.redirectError)
    val process = processBuilder.start
    val standardOutputFuture = captureLines(process.getInputStream)
    val standardErrorFuture = captureLines(process.getErrorStream)
    val exitCodeFuture = captureExitCode(process)
    val compositeFuture = for {
      outputLines <- standardOutputFuture
      errorLines <- standardErrorFuture
      exitCode <- exitCodeFuture
    } yield {
      val ended = clock.instant()
      ProcessOutput(exitCode, outputLines, errorLines, started, ended)
    }
    compositeFuture
  }

  private def updateEnvironment(processBuilder: ProcessBuilderContract, environment: Map[String, String]) = {
    for {
      (key, value) <- environment
    } {
      processBuilder.environment.put(key, value)
    }
  }

  private def captureLines(inputStream: InputStream): Future[Seq[String]] = {
    futureRunner.runInFuture {
      val linesBuffer = new ArrayBuffer[String]
      val inputStreamReader = new InputStreamReader(inputStream, charset)
      val bufferedReader = new BufferedReader(inputStreamReader)
      var line = bufferedReader.readLine()
      while (line != null) {
        linesBuffer.append(line)
        line = bufferedReader.readLine()
      }
      linesBuffer
    }
  }

  private def captureExitCode(process: ProcessContract): Future[Int] = {
    futureRunner.runInFuture {
      process.waitFor
    }
  }
}
