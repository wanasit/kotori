package com.github.wanasit.kotori.benchmark

import com.github.wanasit.kotori.AnyTokenizer
import com.github.wanasit.kotori.Token
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.optimized.PlainToken
import com.github.wanasit.kotori.sudachi.Sudachi

object Tokenizers {

    class EmptyFeatures

    fun loadKuromojiIpadicTokenizer() : AnyTokenizer {
        val innerTokenizer = com.atilika.kuromoji.ipadic.Tokenizer()
        return object : Tokenizer<EmptyFeatures> {
            override fun tokenize(text: String): List<Token<EmptyFeatures>> =
                    innerTokenizer.tokenize(text).map { PlainToken<EmptyFeatures>(it.surface, it.position) }

            override fun toString(): String {
                return innerTokenizer.toString()
            }
        }
    }

    fun loadSudachiTokenizer(): AnyTokenizer {
        return Sudachi.loadSudachiTokenizer()
    }
}