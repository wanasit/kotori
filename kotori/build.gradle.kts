import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
    `maven-publish`
}

repositories {
    jcenter()
}

// compile bytecode to java 8 (default is java 6)
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(Kotlin.Dependencies.stdlib)

    testImplementation(Kotlin.Dependencies.test)
    testImplementation(Kotlin.Dependencies.testJunit)
}


publishing {
    publications {
        create<MavenPublication>(name) {
            groupId = Kotori.groupId
            version = Kotori.version
            artifactId = name

            from(components["java"])

            pom {
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
            }
        }
    }
}