plugins {
    `java-library`
    kotlin("jvm")
    `maven-publish`
}

task<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

dependencies {
    implementation(Kotlin.Dependencies.stdlib)

    testImplementation(Kotlin.Dependencies.test)
    testImplementation(Kotlin.Dependencies.testJunit)
    testImplementation(Kuromoji.Dependencies.Kuromoji_IPADIC)
}

publishing {
    publications {
        val sourcesJar by tasks
        register(project.name, MavenPublication::class) {
            groupId = Kotori.groupId
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