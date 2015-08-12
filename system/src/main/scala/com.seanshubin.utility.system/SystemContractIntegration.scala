package com.seanshubin.utility.system

import java.util.{Map => JavaMap, Properties}

/*
This is part of a "contract" and "contract integration" pattern
The sole purpose of these types of classes is to integrate the environment outside of our control with a contract (trait/interface/protocol)
What belongs here are methods that are a one-to-one pass through to the system implementation
What does NOT belong here is logic, wrappers, helpers, utilities, method chaining, etc.
If you need any of that, put them in a separate cass that delegates to this one
The contract can be used when implementing fakes, mocks, and stubs
Proper abstractions can be implemented with full test coverage by delegating to these low level wrappers
*/
class SystemContractIntegration extends SystemContract {
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
