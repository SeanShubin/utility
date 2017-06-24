package com.seanshubin.utility.exec

import java.io.{ByteArrayOutputStream, InputStream, OutputStream}
import java.util.concurrent.TimeUnit

class ProcessStub extends ProcessContract {
  override def getOutputStream: OutputStream = new ByteArrayOutputStream()

  override def getInputStream: InputStream = ???

  override def getErrorStream: InputStream = ???

  override def waitFor: Int = ???

  override def waitFor(timeout: Long, unit: TimeUnit): Boolean = ???

  override def exitValue: Int = ???

  override def destroy(): Unit = ???

  override def destroyForcibly: ProcessContract = ???

  override def isAlive: Boolean = ???
}
