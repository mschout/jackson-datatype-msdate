# jackson-datatype-msdate

A [Jackson](https://github.com/FasterXML/jackson) datatype module that adds support for serializing and deserializing Microsoft-style JSON date formats (`/Date(ticks+offset)/`) used by .NET and Microsoft REST APIs.

This module handles conversion between `java.time.OffsetDateTime` and the Microsoft date format automatically when registered with a Jackson `ObjectMapper`.

## Microsoft Date Format

The Microsoft date format encodes dates as:

```
/Date(ticks[+-]offset)/
```

Where:
- **ticks** is the number of milliseconds since the Unix epoch (1970-01-01T00:00:00Z). Can be negative for dates before the epoch.
- **offset** is an optional timezone offset in `+HHmm` or `-HHmm` format. If omitted, UTC is assumed.

Examples:
- `/Date(1705305000000+0530)/` - January 15, 2024, 10:30:00 IST
- `/Date(1717243200000-0500)/` - June 1, 2024, 12:00:00 EST
- `/Date(1705305000000)/` - UTC (no offset)
- `/Date(-86400000+0000)/` - December 31, 1969

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.mschout:jackson-datatype-msdate:<version>")
}
```

### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'io.github.mschout:jackson-datatype-msdate:<version>'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.mschout</groupId>
    <artifactId>jackson-datatype-msdate</artifactId>
    <version>VERSION</version>
</dependency>
```

## Usage

Register the module with your `ObjectMapper`:

```kotlin
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.mschout.jackson.datatype.msdate.MicrosoftDateModule

val mapper = ObjectMapper().registerModule(MicrosoftDateModule())
```

Once registered, `OffsetDateTime` fields are automatically serialized and deserialized using the Microsoft date format.

### Deserialization

```kotlin
data class Event(val date: OffsetDateTime)

val json = """{"date":"/Date(1705305000000+0530)/"}"""
val event = mapper.readValue(json, Event::class.java)
// event.date == 2024-01-15T10:30:00+05:30
```

### Serialization

```kotlin
import java.time.OffsetDateTime
import java.time.ZoneOffset

val event = Event(date = OffsetDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneOffset.ofHoursMinutes(5, 30)))
val json = mapper.writeValueAsString(event)
// json == {"date":"/Date(1705305000000+0530)/"}
```

## Requirements

- Java 17+
- Jackson 2.x

## License

See [LICENSE](LICENSE) for details.
