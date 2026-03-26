package io.github.mschout.jackson.datatype.msdate

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * A custom deserializer for parsing Microsoft-style date strings into `OffsetDateTime` objects.
 *
 * The Microsoft date format represents dates as `"/Date(ticks[+-]offset)/"`, where:
 * - `ticks` is the number of milliseconds since the Unix epoch (1970-01-01T00:00:00Z).
 * - `offset` is an optional time zone offset in the format `+HHmm` or `-HHmm`.
 *
 * This deserializer extracts the ticks and time zone offset from the input string, converts the
 * ticks to an `Instant`, and applies the parsed offset to produce an `OffsetDateTime`.
 *
 * If the input string is null or fails to match the expected format, an exception is thrown.
 *
 * This deserializer is compatible with Jackson's `ObjectMapper` and can be registered via a custom
 * module, such as `MicrosoftDateModule`.
 */
class MicrosoftDateDeserializer : JsonDeserializer<OffsetDateTime>() {
  override fun deserialize(p: JsonParser, ctx: DeserializationContext): OffsetDateTime? {
    val text = p.text ?: return null
    val match =
        MICROSOFT_DATE_PATTERN.matchEntire(text)
            ?: throw ctx.weirdStringException(text, OffsetDateTime::class.java, PARSE_ERROR_MSG)

    val ticks = match.groupValues[1].toLong()
    val instant = Instant.ofEpochMilli(ticks)

    val offsetStr = match.groupValues[2]
    val offset =
        if (offsetStr.isEmpty()) {
          ZoneOffset.UTC
        } else {
          val sign = if (offsetStr[0] == '+') 1 else -1
          val hours = offsetStr.substring(1, 3).toInt()
          val minutes = offsetStr.substring(3, 5).toInt()
          ZoneOffset.ofHoursMinutes(sign * hours, sign * minutes)
        }

    return instant.atOffset(offset)
  }

  companion object {
    private val MICROSOFT_DATE_PATTERN = Regex("""/Date\((-?\d+)([+-]\d{4})?\)/""")
    private const val PARSE_ERROR_MSG = "Expected Microsoft date format: /Date(ticks[+-]offset)/"
  }
}
