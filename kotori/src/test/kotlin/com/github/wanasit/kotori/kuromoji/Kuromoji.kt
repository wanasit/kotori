package com.github.wanasit.kotori.kuromoji

import com.github.wanasit.kotori.Token
import com.github.wanasit.kotori.Tokenizer


object Kuromoji {

    class KuromojiTokenFeature

    class KuromojiToken(override val text: String, override val index: Int,
                        override val features: KuromojiTokenFeature = KuromojiTokenFeature()
    ) : Token<KuromojiTokenFeature>

    fun loadTokenizer() : Tokenizer<KuromojiTokenFeature> {
        val innerTokenizer = com.atilika.kuromoji.ipadic.Tokenizer()
        return object : Tokenizer<KuromojiTokenFeature> {
            override fun tokenize(text: String): List<Token<KuromojiTokenFeature>> =
                    innerTokenizer.tokenize(text).map { KuromojiToken(it.surface, it.position) }

            override fun toString(): String {
                return innerTokenizer.toString()
            }
        }
    }
}

