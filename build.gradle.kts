import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.palantir.gradle.gitversion.VersionDetails

plugins {
    java
    kotlin("jvm") version Kotlin.version

    id("com.palantir.git-version") version "0.12.3"

    id(Release.MavenPublish.plugin)
    id(Release.Bintray.plugin) version Release.Bintray.version
}

// Do not publish root project
bintray {
    setPublications()
    setConfigurations()
    with(pkg) {
        repo = Kotori.Package.repo
        name = Kotori.Package.name
    }
}


allprojects {
    repositories {
        jcenter()
    }

    // compile bytecode to java 8 (default is java 6)
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val versionDetails: groovy.lang.Closure<*> by extra
val gitVersionDetails: VersionDetails = versionDetails() as VersionDetails
val publishProjectPaths = listOf(":kotori", ":kotori-sudachi")
subprojects {
    version = gitVersionDetails.lastTag
    group = Kotori.groupId

    if (project.path in publishProjectPaths) {

        apply {
            plugin("java-library")
            plugin(Release.MavenPublish.plugin)
            plugin(Release.Bintray.plugin)
        }

        task<Jar>("sourcesJar") {
            from(sourceSets["main"].allSource)
            archiveClassifier.set("sources")
        }


        bintray {
            user = findProperty("BINTRAY_USER") as? String
            key = findProperty("BINTRAY_KEY") as? String
            publish = true
            override = true

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

        val sourcesJar by tasks
        publishing {
            publications {
                register(project.name, MavenPublication::class) {
                    from(components["java"])
                    artifact(sourcesJar)

                    groupId = project.group as String
                    version = project.version as String?
                    artifactId = project.name

                    pom {
                        name.set(project.name)
                        description.set(Kotori.Package.desc)
                        packaging = "jar"
                        url.set(Kotori.Package.url)

                        licenses {
                            license {
                                name.set("MIT License")
                                url.set("http://www.opensource.org/licenses/mit-license.php")
                            }
                        }
                        scm {
                            url.set(Kotori.Package.url)
                            connection.set(Kotori.Package.scm)
                            developerConnection.set(Kotori.Package.scm)
                        }
                    }
                }
            }
        }
    }
}

task("prepareTestingData") {
    dependsOn(
            ":kotori-dictionaries:downloadSudachiSmallDict",
            ":kotori-dictionaries:downloadMecabIpadic",
            ":kotori-benchmark:downloadLivedoorNews",
            ":kotori-benchmark:downloadTatoeba"
    )
}