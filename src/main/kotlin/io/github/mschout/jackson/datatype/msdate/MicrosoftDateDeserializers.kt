/*
 * Copyright 2026 Michael Schout
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mschout.jackson.datatype.msdate

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import io.github.mschout.microsoft.json.date.MicrosoftJsonDateParser
import java.time.LocalDate
import java.time.OffsetDateTime

private const val PARSE_ERROR_MSG = "Expected Microsoft date format: /Date(ticks[+-]offset)/"

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
class MicrosoftDateOffsetDateTimeDeserializer : JsonDeserializer<OffsetDateTime>() {
  private val parser = MicrosoftJsonDateParser()

  override fun deserialize(p: JsonParser, ctx: DeserializationContext): OffsetDateTime? {
    val text = p.text ?: return null

    try {
      return parser.parse(text)?.offsetDateTime
    } catch (_: IllegalArgumentException) {
      throw ctx.weirdStringException(text, OffsetDateTime::class.java, PARSE_ERROR_MSG)
    }
  }
}

class MicrosoftDateLocalDateDeserializer : JsonDeserializer<LocalDate>() {
  private val parser = MicrosoftJsonDateParser()

  override fun deserialize(p: JsonParser, ctx: DeserializationContext): LocalDate? {
    val text = p.text ?: return null

    try {
      return parser.parse(text)?.offsetDateTime?.toLocalDate()
    } catch (_: IllegalArgumentException) {
      throw ctx.weirdStringException(text, OffsetDateTime::class.java, PARSE_ERROR_MSG)
    }
  }
}
