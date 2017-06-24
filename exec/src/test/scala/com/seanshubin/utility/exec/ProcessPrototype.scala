package com.seanshubin.utility.exec

import java.io.{BufferedReader, InputStreamReader}
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

//import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object ProcessPrototype extends App {
  val command = Seq("pwd")
  val workingDirectory = Paths.get("/Users/sshubin")
  val redirectErrorToStandard = true
  val processBuilder = new ProcessBuilder(command: _*).
    directory(workingDirectory.toFile).
    redirectErrorStream(redirectErrorToStandard)
  val process = processBuilder.start()
  val charset = StandardCharsets.UTF_8
  val standardOutputLine: String => Unit = println
  val errorOutputLine: String => Unit = println
  implicit val executionContext = new StubExecutionContext

  def runInFuture[T](block: => T): Future[T] = {
    Future {
      block
    }
  }

  private val standardOutputFuture = runInFuture {
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream, charset))
    var line = reader.readLine()
    while (line != null) {
      standardOutputLine(line)
      line = reader.readLine()
    }
    println("standard output done")
  }
  private val errorOutputFuture = runInFuture {
    val reader = new BufferedReader(new InputStreamReader(process.getErrorStream, charset))
    var line = reader.readLine()
    while (line != null) {
      errorOutputLine(line)
      line = reader.readLine()
    }
    println("error output done")
  }

  val exitCodeFuture = runInFuture {
    process.waitFor
  }
  val compositeFuture = for {
    _ <- errorOutputFuture
    _ <- standardOutputFuture
    exitCode <- exitCodeFuture
  } yield {
    exitCode
  }

  executionContext.runAll()

  val result = Await.result(compositeFuture, Duration.Inf)

  println(result)
}
