plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(project(":kotori"))
    implementation(Sudachi.Dependencies.Sudachi)

    implementation(Kotlin.Dependencies.Stdlib)
    implementation(Kotlin.Dependencies.Reflect)

    testImplementation(Kotlin.Dependencies.Test)
    testImplementation(Kotlin.Dependencies.TestJunit)
}
