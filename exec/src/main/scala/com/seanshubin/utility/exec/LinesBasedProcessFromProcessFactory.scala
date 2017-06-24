package com.seanshubin.utility.exec

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter, PrintWriter}
import java.nio.charset.Charset
import java.nio.file.Path

import scala.concurrent.{ExecutionContext, Future}

class LinesBasedProcessFromProcessFactory(command: Seq[String],
                                          workingDirectory: Path,
                                          environment: Map[String, String],
                                          standardOutputLine: String => Unit,
                                          errorOutputLine: String => Unit,
                                          redirectErrorToStandard: Boolean,
                                          charset: Charset,
                                          processFactory: ProcessFactory,
                                          executionContext: ExecutionContext) extends LinesBasedProcess {
  private implicit val implicitExecutionContext = executionContext
  private val process = processFactory.createProcess(command, workingDirectory, environment, redirectErrorToStandard)
  private val standardOutputFuture: Future[Unit] = Future {
    println("a")
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream, charset))
    println("b")
    var line = reader.readLine()
    println("c")
    while (line != null) {
      println("d")
      standardOutputLine(line)
      line = reader.readLine()
    }
  }
  private val errorOutputFuture: Future[Unit] = Future {
    println("e")
    val reader = new BufferedReader(new InputStreamReader(process.getErrorStream, charset))
    println("f")
    var line = reader.readLine()
    println("g")
    while (line != null) {
      println("h")
      errorOutputLine(line)
      line = reader.readLine()
    }
  }
  private val processInput = new PrintWriter(new OutputStreamWriter(process.getOutputStream, charset))

  override def sendLine(line: String): Unit = {
    processInput.println(line)
    processInput.flush()
  }

  override def waitForExitCode(): Future[Int] = {
    val exitCodeFuture = Future {
      process.waitFor
    }
    val compositeFuture = for {
      _ <- errorOutputFuture
      _ <- standardOutputFuture
      exitCode <- exitCodeFuture
    } yield {
      exitCode
    }
    compositeFuture
  }
}
