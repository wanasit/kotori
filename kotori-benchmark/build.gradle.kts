plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(project(":kotori"))
    implementation(project(":kotori-dictionaries"))
    implementation(project(":kotori-sudachi"))

    implementation(Kuromoji.Dependencies.Kuromoji_IPADIC)
    implementation("com.github.ajalt:clikt:2.7.1")
    implementation("org.rauschig:jarchivelib:0.7.1")

    implementation(Kotlin.Dependencies.Stdlib)
    implementation(Kotlin.Dependencies.Reflect)

    testImplementation(Kotlin.Dependencies.Test)
    testImplementation(Kotlin.Dependencies.TestJunit)
}

task<JavaExec>("downloadLivedoorNews") {
    args = listOf("livedoor-news")
    main = "com.github.wanasit.kotori.benchmark.DownloadDatasetKt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("downloadTatoeba") {
    args = listOf("tatoeba")
    main = "com.github.wanasit.kotori.benchmark.DownloadDatasetKt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("runBenchmark") {
    dependsOn(":prepareTestingData")
    main = "com.github.wanasit.kotori.benchmark.BenchmarkKt"
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.withType<Test> {
    dependsOn(":prepareTestingData")
    maxHeapSize = "2048m"
}