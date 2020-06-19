// Library version
object Kotori {
    const val groupId = "com.github.wanasit.kotori"
    const val version = "0.0.3"

    object Package {
        const val repo = "maven"
        const val name = "Kotori"
        const val desc = "A Japanese tokenizer and morphological analysis engine written in Kotlin"
        const val userOrg = "wanasit"
        const val url = "https://github.com/wanasit/kotori"
        const val scm = "git@github.com:wanasit/kotori.git"
        const val licenseName = "MIT License"
    }
}

object Kotlin {
    const val version = "1.3.70"
    object Dependencies {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"

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


object Release {
    object MavenPublish {
        const val plugin = "maven-publish"
    }

    object Bintray {
        const val version = "1.8.4"
        const val plugin = "com.jfrog.bintray"
    }
}

object Data {

    const val SudachiDictVersion = "sudachi-dictionary-20200330"
    const val MecabIpadicVersion = "mecab-ipadic-2.7.0-20070801"
}