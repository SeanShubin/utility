package com.seanshubin.utility.exec.test.app

import java.io._
import java.nio.charset.StandardCharsets

object ExecTestApp extends App {
  val charset = StandardCharsets.UTF_8
  val in = new BufferedReader(new InputStreamReader(System.in, charset))
  val ExitRegex ="""exit (\d+)""".r
  val OutRegex = """out (\d+)""".r
  val ErrRegex = """err (\d+)""".r
  val syntaxLines = Seq(
    "Example commands:",
    "  exit 3",
    "    exits with the provided exit code",
    "  out 4",
    "    sends the provided number of lines to standard output",
    "  err 5",
    "    sends the provided number of lines to standard error"
  )
  val argumentLines = s"Command line arguments: ${args.size}" +: args.zipWithIndex.map(formatArgument)

  arguments()
  syntax()
  processRemainingLines()

  def processRemainingLines(): Unit = {
    val line = in.readLine()
    try {
      line match {
        case ExitRegex(exitCodeString) => exit(exitCodeString)
        case OutRegex(quantityString) =>
          out(quantityString)
          processRemainingLines()
        case ErrRegex(quantityString) =>
          err(quantityString)
          processRemainingLines()
        case _ =>
          syntax()
          processRemainingLines()
      }
    } catch {
      case ex: Exception =>
        badCommand(line)
        processRemainingLines()
    }
  }


  def composeLines(quantity: Int, prefix: String): Seq[String] = {
    val lines = for {
      i <- 1 to quantity
    } yield {
      s"$prefix $i"
    }
    lines
  }

  def send(quantityString: String, prefix: String, out: OutputStream): Unit = {
    val quantity = quantityString.toInt
    val printWriter = new PrintWriter(new OutputStreamWriter(out, charset))
    val lines = composeLines(quantity, prefix)
    lines.foreach(printWriter.println)
    printWriter.flush()
  }

  def sendLines(lines: Seq[String], out: OutputStream): Unit = {
    val printWriter = new PrintWriter(new OutputStreamWriter(out, charset))
    lines.foreach(printWriter.println)
  }

  def out(quantityString: String): Unit = {
    send(quantityString, "standard output", System.out)
  }

  def err(quantityString: String): Unit = {
    send(quantityString, "error output", System.err)
  }

  def exit(exitCodeString: String): Unit = {
    val exitCode = exitCodeString.toInt
    System.exit(exitCode)
  }

  def syntax(): Unit = {
    syntaxLines.foreach(println)
  }

  def arguments(): Unit = {
    argumentLines.foreach(println)
  }

  def syntax(badCommand: String): Unit = {
    println(s"Unable to execute: $badCommand")
    syntax()
  }

  def badCommand(command: String): Unit = {
    val header = s"Unable to execute: $command"
    val lines = header +: syntaxLines
    sendLines(lines, System.err)
  }

  def formatArgument(entry: (String, Int)): String = {
    val (arg, index) = entry
    s"  argument[$index] = '$arg'"
  }
}
