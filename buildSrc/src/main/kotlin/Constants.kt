// Library version
object Kotori {
    const val groupId = "com.github.wanasit.kotori"
    const val version = "0.0.1"
}

object Kotlin {
    const val version = "1.3.70"
    object Dependencies {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"

        const val test = "org.jetbrains.kotlin:kotlin-test:$version"
        const val testJunit = "org.jetbrains.kotlin:kotlin-test-junit:$version"
    }
}

object Kuromoji {
    const val version = "0.9.0"
    object Dependencies {
        const val Kuromoji_IPADIC = "com.atilika.kuromoji:kuromoji-ipadic:$version"
    }
}


object Data {

    const val SudachiDictVersion = "sudachi-dictionary-20200330"
    const val MecabIpadicVersion = "mecab-ipadic-2.7.0-20070801"
}