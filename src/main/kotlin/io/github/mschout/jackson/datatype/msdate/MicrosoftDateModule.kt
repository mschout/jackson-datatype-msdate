package io.github.mschout.jackson.datatype.msdate

import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.OffsetDateTime

/**
 * A custom Jackson module that provides support for serializing and deserializing Microsoft-style
 * date formats. This module configures Jackson to handle `OffsetDateTime` instances in a format
 * specific to Microsoft's serialization style.
 *
 * The module registers:
 * - A custom serializer (`MicrosoftDateSerializer`) for converting `OffsetDateTime` objects into
 *   the Microsoft date format.
 * - A custom deserializer (`MicrosoftDateDeserializer`) for parsing Microsoft date format strings
 *   into `OffsetDateTime` objects.
 *
 * This date format typically represents dates as `"/Date(ticks[+-]offset)/"`, where:
 * - `ticks` is the number of milliseconds since the Unix epoch (1970-01-01T00:00:00Z).
 * - `offset` is an optional time zone offset in the format `+HHmm` or `-HHmm`.
 *
 * Usage of this module ensures seamless conversion between `OffsetDateTime` and the
 * Microsoft-specific date notation during JSON serialization and deserialization.
 */
class MicrosoftDateModule : SimpleModule("MicrosoftDateModule") {
  init {
    addDeserializer(OffsetDateTime::class.java, MicrosoftDateDeserializer())
    addSerializer(OffsetDateTime::class.java, MicrosoftDateSerializer())
  }
}
