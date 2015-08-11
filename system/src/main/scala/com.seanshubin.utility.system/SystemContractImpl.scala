package com.seanshubin.utility.system

import java.util.{Map => JavaMap, Properties}

class SystemContractImpl extends SystemContract {
  override def currentTimeMillis: Long = System.currentTimeMillis()

  override def runFinalization(): Unit = System.runFinalization()

  override def gc(): Unit = System.gc()

  override def getProperty(key: String): String = System.getProperty(key)

  override def getProperty(key: String, default: String): String = System.getProperty(key, default)

  override def setProperty(key: String, value: String): String = System.setProperty(key, value)

  override def setProperties(props: Properties): Unit = System.setProperties(props)

  override def nanoTime: Long = System.nanoTime()

  override def clearProperty(key: String): String = System.clearProperty(key)

  override def getenv(name: String): String = System.getenv(name)

  override def getenv: JavaMap[String, String] = System.getenv()

  override def exit(status: Int): Unit = System.exit(status)

  override def lineSeparator: String = System.lineSeparator()

  override def getProperties: Properties = System.getProperties
}
