package com.seanshubin.utility.exec

import java.nio.file.Path

class ProcessFactoryStub(process: ProcessContract) extends ProcessFactory {
  override def createProcess(command: Seq[String], workingDirectory: Path, environment: Map[String, String], redirectErrorToStandard: Boolean): ProcessContract = process
}
