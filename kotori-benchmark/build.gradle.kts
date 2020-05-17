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

    implementation("com.atilika.kuromoji:kuromoji-ipadic:0.9.0")
    implementation("com.worksap.nlp:sudachi:0.4.0")
    implementation("com.beust:klaxon:5.0.1")

    implementation(Kotlin.Dependencies.stdlib)

    testImplementation(Kotlin.Dependencies.test)
    testImplementation(Kotlin.Dependencies.testJunit)
}

task<JavaExec>("runBenchmark") {

    dependsOn(":prepareData")

    main = "com.github.wanasit.kotori.benchmark.BenchmarkKt"
    classpath = sourceSets["main"].runtimeClasspath
}
