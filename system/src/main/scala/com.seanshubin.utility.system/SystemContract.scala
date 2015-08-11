package com.seanshubin.utility.system

import java.util.{Map => JavaMap, Properties}

trait SystemContract {
  def currentTimeMillis: Long

  def nanoTime: Long

  def getProperties: Properties

  def lineSeparator: String

  def setProperties(props: Properties)

  def getProperty(key: String): String

  def getProperty(key: String, default: String): String

  def setProperty(key: String, value: String): String

  def clearProperty(key: String): String

  def getenv(name: String): String

  def getenv: JavaMap[String, String]

  def exit(status: Int): Unit

  def gc(): Unit

  def runFinalization(): Unit
}
