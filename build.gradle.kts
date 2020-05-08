
plugins {
    java
    kotlin("jvm") version Kotlin.version apply false
    `maven-publish`
}

repositories {
    jcenter()
    maven(url="https://jitpack.io")
}

