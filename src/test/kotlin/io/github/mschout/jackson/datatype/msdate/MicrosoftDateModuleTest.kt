package io.github.mschout.jackson.datatype.msdate

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MicrosoftDateModuleTest :
    FunSpec({
      test("module registers with ObjectMapper") {
        val mapper = ObjectMapper().registerModule(MicrosoftDateModule())
        val dt = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        val json = mapper.writeValueAsString(dt)
        val result = mapper.readValue(json, OffsetDateTime::class.java)
        result shouldBe dt
      }

      test("round-trip with positive offset") {
        val mapper = ObjectMapper().registerModule(MicrosoftDateModule())
        val dt = OffsetDateTime.of(2024, 6, 15, 14, 30, 0, 0, ZoneOffset.ofHours(5))
        val json = mapper.writeValueAsString(dt)
        val result = mapper.readValue(json, OffsetDateTime::class.java)
        result shouldBe dt
      }

      test("round-trip with negative offset") {
        val mapper = ObjectMapper().registerModule(MicrosoftDateModule())
        val dt = OffsetDateTime.of(2024, 12, 25, 8, 0, 0, 0, ZoneOffset.ofHours(-8))
        val json = mapper.writeValueAsString(dt)
        val result = mapper.readValue(json, OffsetDateTime::class.java)
        result shouldBe dt
      }

      test("round-trip with fractional hour offset") {
        val mapper = ObjectMapper().registerModule(MicrosoftDateModule())
        val dt = OffsetDateTime.of(2024, 3, 1, 12, 0, 0, 0, ZoneOffset.ofHoursMinutes(5, 30))
        val json = mapper.writeValueAsString(dt)
        val result = mapper.readValue(json, OffsetDateTime::class.java)
        result shouldBe dt
      }

      test("module name is MicrosoftDateModule") {
        val module = MicrosoftDateModule()
        module.moduleName shouldBe "MicrosoftDateModule"
      }

      test("round-trip serialization in data class") {
        val mapper = ObjectMapper().registerModule(MicrosoftDateModule())
        val dt = OffsetDateTime.of(2024, 7, 4, 18, 0, 0, 0, ZoneOffset.ofHours(-4))
        val json = mapper.writeValueAsString(dt)
        json shouldBe "\"/Date(${dt.toInstant().toEpochMilli()}-0400)/\""
        val result = mapper.readValue(json, OffsetDateTime::class.java)
        result shouldBe dt
      }
    })
