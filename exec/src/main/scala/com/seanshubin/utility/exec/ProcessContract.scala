package com.seanshubin.utility.exec

import java.io._
import java.util.concurrent.TimeUnit

trait ProcessContract {
  def getOutputStream: OutputStream

  def getInputStream: InputStream

  def getErrorStream: InputStream

  def waitFor: Int

  def waitFor(timeout: Long, unit: TimeUnit): Boolean

  def exitValue: Int

  def destroy(): Unit

  def destroyForcibly: ProcessContract

  def isAlive: Boolean
}
