plugins {
    `java-library`
    kotlin("jvm")
}

repositories {
    jcenter()
    maven(url="http://www.atilika.org/nexus/content/repositories/atilika")
}


dependencies {
    implementation(project(":kotori"))
    implementation(project(":kotori-benchmark"))

    implementation(Kuromoji.Dependencies.Kuromoji_IPADIC)

    implementation(Kotlin.Dependencies.stdlib)

    testImplementation(Kotlin.Dependencies.test)
    testImplementation(Kotlin.Dependencies.testJunit)
}

task<JavaExec>("runBenchmark") {

    dependsOn(":prepareData")

    main = "com.github.wanasit.kotori.benchmark.BenchmarkKt"
    classpath = sourceSets["main"].runtimeClasspath
}