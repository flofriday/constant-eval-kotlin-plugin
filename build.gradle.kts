buildscript {
  extra["kotlin_plugin_id"] = "com.flofriday.contant-eval-kotlin-plugin"
}

plugins {
  kotlin("jvm") version "2.0.20" apply false
  id("org.jetbrains.dokka") version "1.9.20" apply false
  id("com.gradle.plugin-publish") version "1.3.0" apply false
  id("com.github.gmazzo.buildconfig") version "5.5.0" apply false
}

allprojects {
  group = "com.flofriday.kotlin"
  version = "0.1.0-SNAPSHOT"
}

subprojects {
  repositories {
    mavenCentral()
  }
}
