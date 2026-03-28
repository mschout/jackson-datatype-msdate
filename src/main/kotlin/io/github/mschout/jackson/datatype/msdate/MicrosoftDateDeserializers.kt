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

  /**
   * Deserializes a JSON string into an `OffsetDateTime` object.
   *
   * This method attempts to parse a Microsoft-style date string, extracting the timestamp and time
   * zone offset components to construct an `OffsetDateTime` instance. If the input string is null,
   * it returns null. If parsing fails, an exception is thrown.
   *
   * @param p The `JsonParser` used to read the JSON input.
   * @param ctx The `DeserializationContext` providing contextual information during
   *   deserialization.
   * @return The deserialized `OffsetDateTime` object, or null if the input string is null.
   * @throws IllegalArgumentException If the string cannot be parsed into an `OffsetDateTime`.
   */
  override fun deserialize(p: JsonParser, ctx: DeserializationContext): OffsetDateTime? {
    val text = p.text ?: return null

    try {
      return parser.parse(text)?.offsetDateTime
    } catch (_: IllegalArgumentException) {
      throw ctx.weirdStringException(text, OffsetDateTime::class.java, PARSE_ERROR_MSG)
    }
  }
}

/**
 * Custom deserializer for converting Microsoft-style date strings into `LocalDate` instances.
 *
 * This deserializer parses date strings formatted using the Microsoft date notation:
 * `"/Date(ticks[+-]offset)/"`. The format represents:
 * - `ticks`: The number of milliseconds since the Unix epoch (1970-01-01T00:00:00Z).
 * - `offset`: An optional time zone offset in the format `+HHmm` or `-HHmm`.
 *
 * The deserialization process relies on the `MicrosoftJsonDateParser` to parse the input string,
 * extract the date components, and convert it to a `LocalDate` by discarding the time zone
 * information.
 *
 * Throws a `JsonMappingException` if the input string cannot be parsed due to invalid format or
 * content.
 */
class MicrosoftDateLocalDateDeserializer : JsonDeserializer<LocalDate>() {
  private val parser = MicrosoftJsonDateParser()

  /**
   * Deserializes a JSON string into a `LocalDate` object.
   *
   * This method extracts the text content from the `JsonParser` and uses the
   * `MicrosoftJsonDateParser` to process date strings formatted in Microsoft's style (e.g.,
   * `"/Date(ticks[+-]offset)/"`). If successful, it returns the corresponding `LocalDate` by
   * discarding time zone information.
   *
   * Throws a `JsonMappingException` when the input string cannot be parsed due to invalid format or
   * content.
   *
   * @param p The `JsonParser` instance for extracting the JSON data.
   * @param ctx The `DeserializationContext` used for handling deserialization-specific operations.
   * @return A `LocalDate` object representing the parsed date, or `null` if the input is empty.
   */
  override fun deserialize(p: JsonParser, ctx: DeserializationContext): LocalDate? {
    val text = p.text ?: return null

    try {
      return parser.parse(text)?.offsetDateTime?.toLocalDate()
    } catch (_: IllegalArgumentException) {
      throw ctx.weirdStringException(text, OffsetDateTime::class.java, PARSE_ERROR_MSG)
    }
  }
}
