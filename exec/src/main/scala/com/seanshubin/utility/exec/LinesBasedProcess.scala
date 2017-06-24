package com.seanshubin.utility.exec

import scala.concurrent.Future

trait LinesBasedProcess {
  def sendLine(line: String): Unit

  def waitForExitCode(): Future[Int]
}
