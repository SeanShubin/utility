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
