package com.github.wanasit.kotori

import com.github.wanasit.kotori.core.LatticeBasedTokenizer

interface Tokenizer {

    fun tokenize(text: String): List<Token>

    companion object {
        @JvmStatic
        fun createDefaultTokenizer(): Tokenizer {
            val defaultDictionary = Dictionary.readDefaultFromResource()
            return LatticeBasedTokenizer(defaultDictionary)
        }
    }
}

interface Token {
    val text: String
    val position: Int
}