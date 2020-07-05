package com.github.wanasit.kotori.kuromoji

import com.github.wanasit.kotori.Token
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.optimized.PlainToken


object Kuromoji {

    class KuromojiTokenFeature

    fun loadTokenizer() : Tokenizer<KuromojiTokenFeature> {
        val innerTokenizer = com.atilika.kuromoji.ipadic.Tokenizer()
        return object : Tokenizer<KuromojiTokenFeature> {
            override fun tokenize(text: String): List<Token<KuromojiTokenFeature>> =
                    innerTokenizer.tokenize(text).map {
                        PlainToken(it.surface, it.position, KuromojiTokenFeature())
                    }

            override fun toString(): String {
                return innerTokenizer.toString()
            }
        }
    }
}

