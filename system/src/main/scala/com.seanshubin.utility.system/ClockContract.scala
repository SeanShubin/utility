package com.seanshubin.utility.system

import java.time._

trait ClockContract {
  def getZone: ZoneId

  def millis: Long

  def instant: Instant
}
