package com.seanshubin.utility.system

import java.time._

trait ClockContract {
  def getZone: ZoneId

  def instant: Instant
}
