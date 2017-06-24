package com.seanshubin.utility.exec

import java.nio.file.Path

class ProcessFactoryUsingProcessBuilder extends ProcessFactory {
  override def createProcess(command: Seq[String],
                             workingDirectory: Path,
                             environment: Map[String, String],
                             redirectErrorToStandard: Boolean): ProcessContract = {
    val processBuilder = new ProcessBuilder(command: _*).directory(workingDirectory.toFile).redirectErrorStream(redirectErrorToStandard)
    val processEnvironment = processBuilder.environment()
    for {
      (key, value) <- environment
    } {
      processEnvironment.put(key, value)
    }
    val process = processBuilder.start()
    new ProcessDelegate(process)
  }
}
