package com.github.wanasit.kotori

import com.github.wanasit.kotori.core.LatticeBasedTokenizer
import com.github.wanasit.kotori.optimized.DefaultTermFeatures

typealias AnyTokenizer = Tokenizer<*>
typealias AnyToken = Token<*>

interface Tokenizer<Features> {

    fun tokenize(text: String): List<Token<Features>>

    companion object {
        @JvmStatic
        fun createDefaultTokenizer(): Tokenizer<DefaultTermFeatures> {
            val defaultDictionary = Dictionary.readDefaultFromResource()
            return LatticeBasedTokenizer(defaultDictionary)
        }

        @JvmStatic
        fun <Features> create(dictionary: Dictionary<Features>): Tokenizer<Features> {
            return LatticeBasedTokenizer(dictionary)
        }
    }
}

interface Token <Features> {
    val text: String
    val index: Int

    val features: Features
}