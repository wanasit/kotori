package com.github.wanasit.kotori.kuromoji

import com.github.wanasit.kotori.Token
import com.github.wanasit.kotori.Tokenizer


object Kuromoji {

    class SimpleToken(override val text: String, override val position: Int) : Token;

    fun loadTokenizer() : Tokenizer {
        val innerTokenizer = com.atilika.kuromoji.ipadic.Tokenizer()
        return object : Tokenizer {
            override fun tokenize(text: String): List<Token> =
                    innerTokenizer.tokenize(text).map { SimpleToken(it.surface, it.position) }

            override fun toString(): String {
                return innerTokenizer.toString()
            }
        }
    }
}

