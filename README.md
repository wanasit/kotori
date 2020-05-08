# kotori
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

You can install Kotori via [Jitpack](https://jitpack.io/#wanasit/kotori). 

```kotlin
repositories {
  ...
  maven(url = "https://www.jitpack.io")
}

dependencies {
  ...
  implementation("com.github.wanasit.kotori:0.0.2")
}
```

