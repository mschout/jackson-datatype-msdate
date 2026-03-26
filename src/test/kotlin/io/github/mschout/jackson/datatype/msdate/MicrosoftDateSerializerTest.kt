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
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MicrosoftDateSerializerTest :
    FunSpec({
      val mapper = ObjectMapper().registerModule(MicrosoftDateModule())

      test("serializes OffsetDateTime with positive offset") {
        val dt = OffsetDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneOffset.ofHoursMinutes(5, 30))
        val json = mapper.writeValueAsString(dt)
        val ticks = dt.toInstant().toEpochMilli()
        json shouldBe "\"/Date(${ticks}+0530)/\""
      }

      test("serializes OffsetDateTime with negative offset") {
        val dt = OffsetDateTime.of(2024, 6, 1, 12, 0, 0, 0, ZoneOffset.ofHours(-5))
        val json = mapper.writeValueAsString(dt)
        val ticks = dt.toInstant().toEpochMilli()
        json shouldBe "\"/Date(${ticks}-0500)/\""
      }

      test("serializes OffsetDateTime at UTC") {
        val dt = OffsetDateTime.of(2024, 3, 15, 0, 0, 0, 0, ZoneOffset.UTC)
        val json = mapper.writeValueAsString(dt)
        val ticks = dt.toInstant().toEpochMilli()
        json shouldBe "\"/Date(${ticks}+0000)/\""
      }

      test("serializes epoch zero") {
        val dt = OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        val json = mapper.writeValueAsString(dt)
        json shouldBe "\"/Date(0+0000)/\""
      }

      test("serializes date before epoch with negative ticks") {
        val dt = OffsetDateTime.of(1969, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC)
        val json = mapper.writeValueAsString(dt)
        val ticks = dt.toInstant().toEpochMilli()
        json shouldBe "\"/Date(${ticks}+0000)/\""
      }

      test("serializes with offset having non-zero minutes") {
        val dt = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHoursMinutes(9, 45))
        val json = mapper.writeValueAsString(dt)
        val ticks = dt.toInstant().toEpochMilli()
        json shouldBe "\"/Date(${ticks}+0945)/\""
      }

      test("serializes with negative offset having non-zero minutes") {
        val dt = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHoursMinutes(-3, -30))
        val json = mapper.writeValueAsString(dt)
        val ticks = dt.toInstant().toEpochMilli()
        json shouldBe "\"/Date(${ticks}-0330)/\""
      }
    })
