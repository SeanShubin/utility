package com.seanshubin.utility.system

import java.time.{Clock, Instant, ZoneId}

class ClockContractImpl(clock: Clock) extends ClockContract {
  override def getZone: ZoneId = clock.getZone

  override def instant: Instant = clock.instant()
}
