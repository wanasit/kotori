plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(project(":kotori"))
    implementation(project(":kotori-benchmark"))

    implementation(Kuromoji.Dependencies.Kuromoji_IPADIC)

    implementation(Kotlin.Dependencies.stdlib)

    testImplementation(Kotlin.Dependencies.test)
    testImplementation(Kotlin.Dependencies.testJunit)
}