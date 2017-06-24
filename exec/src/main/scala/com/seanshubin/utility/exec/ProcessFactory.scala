package com.seanshubin.utility.exec

import java.nio.file.Path

trait ProcessFactory {
  def createProcess(command: Seq[String],
                    workingDirectory: Path,
                    environment: Map[String, String],
                    redirectErrorToStandard: Boolean): ProcessContract
}
