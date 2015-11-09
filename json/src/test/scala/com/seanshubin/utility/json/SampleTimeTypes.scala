package com.seanshubin.utility.json

import java.time._

case class SampleTimeTypes(instant: Instant,
                           duration: Duration,
                           localDateTime: LocalDateTime,
                           localDate: LocalDate,
                           localTime: LocalTime,
                           zoneId: ZoneId,
                           zonedDateTime: ZonedDateTime)