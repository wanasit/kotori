# Kotori
A Japanese tokenizer and morphological analysis engine written in Kotlin

### Usage

```kotlin
import com.github.wanasit.kotori.Tokenizer

fun main(args: Array<String>) {
    val tokenizer = Tokenizer.createDefaultTokenizer()
    val words = tokenizer.tokenize("お寿司が食べたい。").map { it.text }

    println(words) // [お, 寿司, が, 食べ, たい, 。]
}
```

### Installation

Kotori packages are hosted by [bintray](https://bintray.com/beta/#/wanasit/maven/Kotori?tab=overview) and JCenter.
You can download and install it via Gradle or Maven.

Gradle:
```groovy
repositories {
    jcenter()
}

dependencies {
    ...
    implementation 'com.github.wanasit.kotori:kotori:0.0.3'
}
```

Maven:
```xml
<dependency>
  <groupId>com.github.wanasit.kotori</groupId>
  <artifactId>kotori</artifactId>
  <version>VERSION_NUMBER</version>
  <type>pom</type>
</dependency>
```

You can also install Kotori via [Jitpack](https://jitpack.io/#wanasit/kotori). 

### Dictionary 

Kotori uses `mecab-ipadic-2.7.0-20070801` as the built-in dictionary.

```kotlin
val dictionary = Dictionary.readDefaultFromResource()
val tokenizer = Tokenizer.create(dictionary)

tokenizer.tokenize("お寿司が食べたい。")
```

It works out-of-box with any Mecab dictionary. For example:
* IPADIC ([2.7.0-20070801](http://atilika.com/releases/mecab-ipadic/mecab-ipadic-2.7.0-20070801.tar.gz))
* UniDic ([2.1.2](http://atilika.com/releases/unidic-mecab/unidic-mecab-2.1.2_src.zip))
* JUMANDIC ([7.0-20130310](http://atilika.com/releases/mecab-jumandic/mecab-jumandic-7.0-20130310.tar.gz))

```kotlin
val dictionary = MeCabDictionary.readFromDirectory("~/Download/mecab-ipadic-2.7.0-20070801")
val tokenizer = Tokenizer.create(dictionary)

tokenizer.tokenize("お寿司が食べたい。")
```

### Performance

Kotori is heavily inspired by [Kuromoji](https://github.com/atilika/kuromoji) and [Sudachi](https://github.com/WorksApplications/Sudachi), 
but its tokenization is optimized to be faster than other JVM-based tokenizers (based-on our unfair benchmark).

The following is statistic from tokenizing Japanese sentences from [Tatoeba](https://tatoeba.org/eng/) 
(193,898 sentences entries, 3,561,854 total characters) on Macbook Pro 2020 (2.4 GHz 8-Core Intel Core i9).

|   |  Token Count  | Time (ns per document) |  Time (ns per token)  |
|---|---:|---:|---:|
|Kuromoji (IPADIC) | 2,264,560 | 10,095 | 864 |
|**Kotori (IPADIC)**   | 2,264,705 | **8,190**| **701** |
|Sudachi (sudachi-dictionary-20200330-small)  | 2,308,873 | 27,352 | 2296 |
|Kotori (sudachi-dictionary-20200330-small)   | 2,157,820 | 13,079 | 1175 |

#### (Speculative) What makes Kotori fast

* **Minimal String.substring() usage**. [After JDK 7](https://www.programcreek.com/2013/09/the-substring-method-in-jdk-6-and-jdk-7/), 
the function makes string copy and has O(n) overhead. Some tokenizers that design before the change (e.g. Kuromoji) still have a lot of substrings.

* **A customized Trie data structure**. 
`TransitionArrayTrie` can be quickly built just-in-time when creating a tokenizer,
but it has pretty good performance on Japanese in UTF-16.

#### (Speculative) What makes Kotori slow

* **Kotori doesn't rely on any pre-built data structure** (e.g. `DoubleArrayTrie`). 
It reads a dictionary as list-of-terms format and builds Trie just-in-time.
This is a design decision to make Kotori open to multiple dictionary formats in exchange for some startup time.

* Kotlin (written by the inexperience library author) is slower than Java, 
mostly, because Kotlin's `Array<T?>` has some overhead comparing to Java's native `T[]`.

#### Benchmark

Benchmark can be run as a gradle task.

```bash
./gradlew benchmark
./gradlew benchmark --args='--tokenizer=kuromoji'
./gradlew benchmark --args='--tokenizer=kotori --dictionary=sudachi-small'
```

Check [the source code](https://github.com/wanasit/kotori/blob/master/kotori-benchmark/src/main/kotlin/com/github/wanasit/kotori/benchmark/Benchmark.kt) 
in `kotori-benchmark` project for more details.

