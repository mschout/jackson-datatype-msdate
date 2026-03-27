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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MicrosoftDateDeserializerTest :
    FunSpec({
      val mapper = ObjectMapper().registerModule(MicrosoftDateModule())

      test("deserializes date with positive offset") {
        val result = mapper.readValue("\"/Date(1705305000000+0530)/\"", OffsetDateTime::class.java)
        val expected =
            Instant.ofEpochMilli(1705305000000L).atOffset(ZoneOffset.ofHoursMinutes(5, 30))
        result shouldBe expected
      }

      test("deserializes date with negative offset") {
        val result = mapper.readValue("\"/Date(1717243200000-0500)/\"", OffsetDateTime::class.java)
        val expected = Instant.ofEpochMilli(1717243200000L).atOffset(ZoneOffset.ofHours(-5))
        result shouldBe expected
      }

      test("deserializes date without offset defaults to UTC") {
        val result = mapper.readValue("\"/Date(1705305000000)/\"", OffsetDateTime::class.java)
        val expected = Instant.ofEpochMilli(1705305000000L).atOffset(ZoneOffset.UTC)
        result shouldBe expected
      }

      test("deserializes epoch zero") {
        val result = mapper.readValue("\"/Date(0+0000)/\"", OffsetDateTime::class.java)
        result shouldBe OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
      }

      test("deserializes negative ticks for dates before epoch") {
        val result = mapper.readValue("\"/Date(-86400000+0000)/\"", OffsetDateTime::class.java)
        val expected = Instant.ofEpochMilli(-86400000L).atOffset(ZoneOffset.UTC)
        result shouldBe expected
      }

      test("throws on malformed input") {
        shouldThrow<InvalidFormatException> {
          mapper.readValue("\"not-a-date\"", OffsetDateTime::class.java)
        }
      }

      test("throws on empty string") {
        shouldThrow<InvalidFormatException> { mapper.readValue("\"\"", OffsetDateTime::class.java) }
      }

      test("throws on partial date format") {
        shouldThrow<InvalidFormatException> {
          mapper.readValue("\"/Date()/\"", OffsetDateTime::class.java)
        }
      }

      test("deserializes date with +0000 offset as UTC") {
        val result = mapper.readValue("\"/Date(0+0000)/\"", OffsetDateTime::class.java)
        result.offset shouldBe ZoneOffset.UTC
      }

      test("deserializes date with negative offset and non-zero minutes") {
        val result = mapper.readValue("\"/Date(1705305000000-0330)/\"", OffsetDateTime::class.java)
        val expected =
            Instant.ofEpochMilli(1705305000000L).atOffset(ZoneOffset.ofHoursMinutes(-3, -30))
        result shouldBe expected
      }

      context("LocalDate deserialization") {
        test("deserializes date with positive offset to LocalDate") {
          val result = mapper.readValue("\"/Date(1705305000000+0530)/\"", LocalDate::class.java)
          // 1705305000000 ms = 2024-01-15T07:30:00Z, with +05:30 offset = 2024-01-15T13:00:00+05:30
          result shouldBe LocalDate.of(2024, 1, 15)
        }

        test("deserializes date with negative offset to LocalDate") {
          val result = mapper.readValue("\"/Date(1717243200000-0500)/\"", LocalDate::class.java)
          // 1717243200000 ms = 2024-06-01T12:00:00Z, with -05:00 = 2024-06-01T07:00:00-05:00
          result shouldBe LocalDate.of(2024, 6, 1)
        }

        test("deserializes date without offset to LocalDate") {
          val result = mapper.readValue("\"/Date(1705305000000)/\"", LocalDate::class.java)
          result shouldBe LocalDate.of(2024, 1, 15)
        }

        test("deserializes epoch zero to LocalDate") {
          val result = mapper.readValue("\"/Date(0+0000)/\"", LocalDate::class.java)
          result shouldBe LocalDate.of(1970, 1, 1)
        }

        test("deserializes negative ticks to LocalDate before epoch") {
          val result = mapper.readValue("\"/Date(-86400000+0000)/\"", LocalDate::class.java)
          result shouldBe LocalDate.of(1969, 12, 31)
        }

        test("throws on malformed input for LocalDate") {
          shouldThrow<InvalidFormatException> {
            mapper.readValue("\"not-a-date\"", LocalDate::class.java)
          }
        }

        test("throws on empty string for LocalDate") {
          shouldThrow<InvalidFormatException> { mapper.readValue("\"\"", LocalDate::class.java) }
        }

        test("throws on partial date format for LocalDate") {
          shouldThrow<InvalidFormatException> {
            mapper.readValue("\"/Date()/\"", LocalDate::class.java)
          }
        }

        test("offset affects resulting LocalDate when crossing day boundary") {
          // 2024-01-15T23:00:00Z with +05:30 = 2024-01-16T04:30:00+05:30 -> LocalDate 2024-01-16
          val ticks =
              OffsetDateTime.of(2024, 1, 15, 23, 0, 0, 0, ZoneOffset.UTC).toInstant().toEpochMilli()
          val result = mapper.readValue("\"/Date(${ticks}+0530)/\"", LocalDate::class.java)
          result shouldBe LocalDate.of(2024, 1, 16)
        }
      }
    })
