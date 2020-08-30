plugins {
    `java-library`
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(Kotlin.Dependencies.Stdlib)

    testImplementation(Kotlin.Dependencies.Test)
    testImplementation(Kotlin.Dependencies.TestJunit)
    testImplementation(Kuromoji.Dependencies.Kuromoji_IPADIC)
}
