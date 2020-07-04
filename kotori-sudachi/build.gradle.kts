plugins {
    `java-library`
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(project(":kotori"))
    implementation(Sudachi.Dependencies.Sudachi)

    implementation(Kotlin.Dependencies.Stdlib)
    implementation(Kotlin.Dependencies.Reflect)

    testImplementation(Kotlin.Dependencies.Test)
    testImplementation(Kotlin.Dependencies.TestJunit)
}

tasks.withType<Test> {
    dependsOn(":prepareTestingData")
    maxHeapSize = "4096m"
}

task<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        val sourcesJar by tasks
        register(project.name, MavenPublication::class) {
            groupId = project.group as String
            version = project.version as String?
            artifactId = project.name

            from(components["java"])
            artifact(sourcesJar)

            pom {
                name.set(project.name)
                description.set(Kotori.Package.desc)
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