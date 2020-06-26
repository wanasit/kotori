plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(project(":kotori"))
    implementation("com.worksap.nlp:sudachi:0.4.0")

    implementation(Kotlin.Dependencies.stdlib)
    implementation(Kotlin.Dependencies.reflect)

    testImplementation(Kotlin.Dependencies.test)
    testImplementation(Kotlin.Dependencies.testJunit)
}
