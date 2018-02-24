package com.seanshubin.utility.exec

import scala.concurrent.Future

trait ProcessLauncher {
  def launch(input: ProcessInput): Future[ProcessOutput]

  def launch(input: ProcessInput, redirectBoth: ProcessBuilder.Redirect): Future[ProcessOutput]

  def launch(input: ProcessInput, redirectInput: ProcessBuilder.Redirect, redirectOutput: ProcessBuilder.Redirect): Future[ProcessOutput]
}
