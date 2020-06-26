plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(project(":kotori"))
    implementation(project(":kotori-benchmark"))

    implementation(Kuromoji.Dependencies.Kuromoji_IPADIC)

    implementation(Kotlin.Dependencies.Stdlib)

    testImplementation(Kotlin.Dependencies.Test)
    testImplementation(Kotlin.Dependencies.TestJunit)
}