package com.github.wanasit.kotori.benchmark

import com.github.wanasit.kotori.Token
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.mecab.MeCabDictionary
import com.github.wanasit.kotori.optimized.SimpleToken
import com.github.wanasit.kotori.sudachi.Sudachi
import java.lang.IllegalStateException

object Tokenizers {

    fun loadKotoriDefaultTokenizer() : Tokenizer {
        return Tokenizer.createDefaultTokenizer()
    }

    fun loadKotoriIpadicTokenizer(
            dictDir : String = "../data/mecab-ipadic-2.7.0-20070801"
    ) : Tokenizer {
        val dictionary = MeCabDictionary.readFromDirectory(dictDir)
        return Tokenizer.create(dictionary)
    }

    fun loadKuromojiIpadicTokenizer() : Tokenizer {
        val innerTokenizer = com.atilika.kuromoji.ipadic.Tokenizer()
        return object : Tokenizer {
            override fun tokenize(text: String): List<Token> =
                    innerTokenizer.tokenize(text).map { SimpleToken(it.surface, it.position) }

            override fun toString(): String {
                return innerTokenizer.toString()
            }
        }
    }

    fun loadKotoriSudachiDictTokenizer(): Tokenizer {
        return Sudachi.loadKotoriTokenizerWithSudachiDict()
    }

    fun loadSudachiTokenizer(): Tokenizer {
        return Sudachi.loadSudachiTokenizer()
    }
}