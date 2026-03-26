package io.github.mschout.jackson.datatype.msdate

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.OffsetDateTime

/**
 * A custom serializer for converting `OffsetDateTime` objects into Microsoft-style date strings.
 *
 * The Microsoft date format represents dates as `"/Date(ticks[+-]offset)/"`, where:
 * - `ticks` is the number of milliseconds since the Unix epoch (1970-01-01T00:00:00Z).
 * - `offset` is the time zone offset in the format `+HHmm` or `-HHmm`.
 *
 * This serializer generates the Microsoft-specific string by calculating the number of ticks
 * (milliseconds since epoch) and formatting the time zone offset as required by the format.
 *
 * The serialized output ensures compatibility with systems or APIs that expect Microsoft-style date
 * representations.
 */
class MicrosoftDateSerializer : JsonSerializer<OffsetDateTime>() {
  override fun serialize(
      value: OffsetDateTime,
      gen: JsonGenerator,
      serializers: SerializerProvider,
  ) {
    val ticks = value.toInstant().toEpochMilli()
    val totalSeconds = value.offset.totalSeconds
    val sign = if (totalSeconds >= 0) "+" else "-"
    val absSeconds = Math.abs(totalSeconds)
    val hours = absSeconds / 3600
    val minutes = (absSeconds % 3600) / 60

    gen.writeString("/Date($ticks$sign${"%02d%02d".format(hours, minutes)})/")
  }
}
