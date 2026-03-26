import com.diffplug.spotless.kotlin.KtfmtStep
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.spotless)
  alias(libs.plugins.version.catalog.update)
  alias(libs.plugins.dokka)
  alias(libs.plugins.maven.publish)
  jacoco
}

group = "io.github.mschout"

version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
  api(libs.jackson.core)
  api(libs.jackson.databind)
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)
}

kotlin { jvmToolchain(17) }

tasks.test {
  useJUnitPlatform()
  dependsOn("spotlessCheck")
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required = true
    html.required = true
  }
}

spotless {
  kotlin {
    ktfmt("0.62").configure {
      it.setBlockIndent(2)
      it.setContinuationIndent(4)
      it.setTrailingCommaManagementStrategy(KtfmtStep.TrailingCommaManagementStrategy.COMPLETE)
      it.setRemoveUnusedImports(true)
    }
    trimTrailingWhitespace()
    endWithNewline()
  }

  kotlinGradle {
    target("*.gradle.kts")
    ktfmt()
  }
}

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
