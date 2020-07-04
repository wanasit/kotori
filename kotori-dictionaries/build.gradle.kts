plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    implementation(project(":kotori"))

    implementation(Kotlin.Dependencies.Stdlib)
    implementation("com.github.ajalt:clikt:2.7.1")
    implementation("org.rauschig:jarchivelib:0.7.1")

    testImplementation(Kotlin.Dependencies.Test)
    testImplementation(Kotlin.Dependencies.TestJunit)
}

task<JavaExec>("downloadMecabIpadic") {
    args = listOf("mecab-ipadic")
    main = "com.github.wanasit.kotori.dictionaries.DownloadDictionaryKt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("downloadMecabUnidic") {
    args = listOf("mecab-unidic")
    main = "com.github.wanasit.kotori.dictionaries.DownloadDictionaryKt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("downloadSudachiSmallDict") {
    args = listOf("sudachi-small")
    main = "com.github.wanasit.kotori.dictionaries.DownloadDictionaryKt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("downloadSudachiCoreDict") {
    args = listOf("sudachi-core")
    main = "com.github.wanasit.kotori.dictionaries.DownloadDictionaryKt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("downloadSudachiFullDict") {
    args = listOf("sudachi-full")
    main = "com.github.wanasit.kotori.dictionaries.DownloadDictionaryKt"
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.withType<Test> {
    dependsOn("downloadMecabIpadic")
    dependsOn("downloadMecabUnidic")
    dependsOn("downloadSudachiSmallDict")
    maxHeapSize = "4096m"
}
