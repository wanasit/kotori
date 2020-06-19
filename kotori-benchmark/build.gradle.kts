plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(project(":kotori"))

    implementation(Kuromoji.Dependencies.Kuromoji_IPADIC)
    implementation("com.worksap.nlp:sudachi:0.4.0")
    implementation("com.beust:klaxon:5.0.1")

    implementation(Kotlin.Dependencies.stdlib)
    implementation(Kotlin.Dependencies.reflect)

    testImplementation(Kotlin.Dependencies.test)
    testImplementation(Kotlin.Dependencies.testJunit)
}

task<JavaExec>("runBenchmark") {

    dependsOn(":prepareData")

    main = "com.github.wanasit.kotori.benchmark.BenchmarkKt"
    classpath = sourceSets["main"].runtimeClasspath
}
