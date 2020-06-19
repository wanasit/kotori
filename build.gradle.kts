import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.palantir.gradle.gitversion.VersionDetails

plugins {
    java
    kotlin("jvm") version Kotlin.version

    id("de.undercouch.download") version "4.0.4"
    id("com.palantir.git-version") version "0.12.3"

    id(Release.MavenPublish.plugin)
    id(Release.Bintray.plugin) version Release.Bintray.version
}

val versionDetails: groovy.lang.Closure<*> by extra
val gitVersionDetails: VersionDetails = versionDetails() as VersionDetails

allprojects {
    version = gitVersionDetails.lastTag
    group = Kotori.groupId

    repositories {
        jcenter()
    }

    // compile bytecode to java 8 (default is java 6)
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    apply {
        plugin(Release.MavenPublish.plugin)
        plugin(Release.Bintray.plugin)
    }

    bintray {
        user = findProperty("BINTRAY_USER") as? String
        key = findProperty("BINTRAY_KEY") as? String
        setPublications(project.name)
        with(pkg) {
            repo = Kotori.Package.repo
            name = Kotori.Package.name
            desc = Kotori.Package.desc
            userOrg = Kotori.Package.userOrg
            websiteUrl = Kotori.Package.url
            vcsUrl = Kotori.Package.url
            setLicenses(Kotori.Package.licenseName)
            with(version) {
                name = project.version as String
            }
        }
    }

    publishing {
        publications {
            // Don't publish any, unless override in sub-project
        }
    }
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
    from(tarTree("data/${Data.MecabIpadicVersion}.tar.gz"))
    into(project.file("data"))
}

task<Copy>("prepareSudachiDict") {
    dependsOn("downloadSudachiDict")
    from(zipTree("data/${Data.SudachiDictVersion}.zip"))
    into(project.file("data"))
}

task<Download>("downloadMecabIpadic") {
    src("http://atilika.com/releases/mecab-ipadic/${Data.MecabIpadicVersion}.tar.gz")
    dest(project.file(("data/${Data.MecabIpadicVersion}.tar.gz")))
    overwrite(false)
}

task<Download>("downloadSudachiDict") {
    src("https://object-storage.tyo2.conoha.io/v1/nc_2520839e1f9641b08211a5c85243124a/sudachi/${Data.SudachiDictVersion}-small.zip")
    dest(project.file(("data/${Data.SudachiDictVersion}.zip")))
    overwrite(false)
}

task<Download>("downloadLivedoorNews") {
    src("https://github.com/wanasit/dataset-japanese/raw/master/livedoor-news/topic-news.json")
    dest(project.file("data/livedoor-news/topic-news.json"))
    overwrite(false)
}