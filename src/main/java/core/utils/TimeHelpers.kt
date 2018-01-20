package core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun Long.toDateTime() = ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.of("UTC+3"))!!