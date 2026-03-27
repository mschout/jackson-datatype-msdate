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
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
  id("mschout.all-conventions")
  alias(libs.plugins.dokka)
  alias(libs.plugins.maven.publish)
}

group = "io.github.mschout"

val gitVersion: groovy.lang.Closure<String> by extra

version = gitVersion()

repositories { mavenCentral() }

dependencies {
  api(libs.jackson.core)
  api(libs.jackson.databind)
  implementation(libs.microsoft.json.date.parser)
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)
}

kotlin { jvmToolchain(17) }

mavenPublishing {
  configure(KotlinJvm(javadocJar = JavadocJar.Dokka("dokkaGenerateModuleHtml")))

  publishToMavenCentral()

  signAllPublications()

  coordinates(group.toString(), "jackson-datatype-msdate", version.toString())

  pom {
    name.set("Jackson Datatype MS Date")
    description.set(
        "Jackson module for serialization/deserialization of Microsoft JSON date format (/Date(ticks+offset)/)"
    )
    url.set("https://github.com/mschout/jackson-datatype-msdate")

    licenses {
      license {
        name.set("The Apache License, Version 2.0")
        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
      }
    }

    developers {
      developer {
        id.set("mschout")
        name.set("Michael Schout")
        url.set("https://github.com/mschout")
      }
    }

    scm {
      url.set("https://github.com/mschout/jackson-datatype-msdate")
      connection.set("scm:git:git://github.com/mschout/jackson-datatype-msdate.git")
      developerConnection.set("scm:git:ssh://git@github.com/mschout/jackson-datatype-msdate.git")
    }
  }
}
