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

import io.github.mschout.microsoft.json.date.MicrosoftJsonDate
import java.time.OffsetDateTime
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

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
class MicrosoftDateOffsetDateTimeSerializer : ValueSerializer<OffsetDateTime>() {
  /**
   * Serializes an `OffsetDateTime` object into a Microsoft-style date string.
   *
   * The Microsoft date format represents dates as `"/Date(ticks[+-]offset)/"`, where:
   * - `ticks` is the number of milliseconds since the Unix epoch (1970-01-01T00:00:00Z).
   * - `offset` is the time zone offset in the format `+HHmm` or `-HHmm`.
   *
   * This method generates the Microsoft-specific string by converting the input `OffsetDateTime`
   * into the required format and writes it to the provided JSON generator.
   *
   * @param value The `OffsetDateTime` object to serialize.
   * @param gen The `JsonGenerator` used to write the JSON output.
   * @param ctxt The `SerializationContext` for accessing configuration and other serializers if
   *   needed.
   */
  override fun serialize(value: OffsetDateTime, gen: JsonGenerator, ctxt: SerializationContext) {
    val msDate = MicrosoftJsonDate(value)
    gen.writeString(msDate.toString())
  }
}
