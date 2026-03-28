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

import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.LocalDate
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
 * Microsoft-specific date notation during JSON serialization and deserialization. In addition,
 * conversion from JSON to LocalDate is supported
 */
class MicrosoftDateModule : SimpleModule("MicrosoftDateModule") {
  init {
    // MicrosoftDate <-> OffsetDateTime
    addDeserializer(OffsetDateTime::class.java, MicrosoftDateOffsetDateTimeDeserializer())
    addSerializer(OffsetDateTime::class.java, MicrosoftDateOffsetDateTimeSerializer())

    // MicrosoftDate -> LocalDate
    addDeserializer(LocalDate::class.java, MicrosoftDateLocalDateDeserializer())
  }
}
