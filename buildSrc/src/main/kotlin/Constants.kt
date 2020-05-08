// Library version
object Kotori {
    const val groupId = "com.github.wanasit.kotori"
    const val name = ":kotori"
}

object Kotlin {
    const val version = "1.3.70"
    object Dependencies {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"

        const val test = "org.jetbrains.kotlin:kotlin-test:$version"
        const val testJunit = "org.jetbrains.kotlin:kotlin-test-junit:$version"
    }
}

