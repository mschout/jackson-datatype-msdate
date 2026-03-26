package io.github.mschout.jackson.datatype.msdate

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
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
    })
