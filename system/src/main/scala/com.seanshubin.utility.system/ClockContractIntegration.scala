package com.seanshubin.utility.system

import java.time.{Clock, Instant, ZoneId}

/*
This is a "contract integration" class
The sole purpose of these types of classes is to integrate the environment outside of our control with a contract (trait/interface/protocol)
What belongs here are methods that are a one-to-one pass through to the system implementation
What does NOT belong here is logic, wrappers, helpers, utilities, method chaining, etc.
If you need any of that, put them in a separate cass that delegates to this one
The contract can be used when implementing fakes, mocks, and stubs
*/
class ClockContractIntegration(clock: Clock) extends ClockContract {
  override def getZone: ZoneId = clock.getZone

  override def instant: Instant = clock.instant()
}
