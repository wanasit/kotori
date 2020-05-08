
plugins {
    java
    kotlin("jvm") version Kotlin.version apply false

    maven
}

repositories {
    jcenter()
    maven(url="https://jitpack.io")
}

