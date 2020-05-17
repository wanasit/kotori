import de.undercouch.gradle.tasks.download.Download

plugins {
    java
    kotlin("jvm") version Kotlin.version apply false
    `maven-publish`

    id("de.undercouch.download") version "4.0.4"
}

repositories {
    jcenter()
}

task("prepareData") {
    dependsOn(
            "prepareMecabIpadic",
            "prepareSudachiDict",
            "downloadLivedoorNews"
    )
}

task<Copy>("prepareMecabIpadic") {
    dependsOn("downloadMecabIpadic")
    from(tarTree("data/${Data.mecabIpadicVersion}.tar.gz"))
    into(project.file("data"))
}

task<Copy>("prepareSudachiDict") {
    dependsOn("downloadSudachiDict")
    from(zipTree("data/${Data.sudachiDictVersion}.zip"))
    into(project.file("data"))
}

task<Download>("downloadMecabIpadic") {
    src("http://atilika.com/releases/mecab-ipadic/${Data.mecabIpadicVersion}.tar.gz")
    dest(project.file(("data/${Data.mecabIpadicVersion}.tar.gz")))
    overwrite(false)
}

task<Download>("downloadSudachiDict") {
    src("https://object-storage.tyo2.conoha.io/v1/nc_2520839e1f9641b08211a5c85243124a/sudachi/${Data.sudachiDictVersion}-small.zip")
    dest(project.file(("data/${Data.sudachiDictVersion}.zip")))
    overwrite(false)
}

task<Download>("downloadLivedoorNews") {
    src("https://github.com/wanasit/dataset-japanese/raw/master/livedoor-news/topic-news.json")
    dest(project.file("data/livedoor-news/topic-news.json"))
    overwrite(false)
}