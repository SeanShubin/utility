package com.seanshubin.utility.exec

import scala.concurrent.Future

trait ProcessLauncher {
  def launch(input: ProcessInput): Future[ProcessOutput]
}
