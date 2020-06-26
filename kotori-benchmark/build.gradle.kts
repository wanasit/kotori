plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(project(":kotori"))
    implementation(project(":kotori-sudachi"))

    implementation(Kuromoji.Dependencies.Kuromoji_IPADIC)
    implementation("com.beust:klaxon:5.0.1")

    implementation(Kotlin.Dependencies.Stdlib)
    implementation(Kotlin.Dependencies.Reflect)

    testImplementation(Kotlin.Dependencies.Test)
    testImplementation(Kotlin.Dependencies.TestJunit)
}

task<JavaExec>("runBenchmark") {

    dependsOn(":prepareData")

    main = "com.github.wanasit.kotori.benchmark.BenchmarkKt"
    classpath = sourceSets["main"].runtimeClasspath
}
