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
