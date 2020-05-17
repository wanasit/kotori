package com.github.wanasit.kotori.benchmark

import com.github.wanasit.kotori.Token
import com.github.wanasit.kotori.Tokenizer
import java.lang.IllegalStateException

object Tokenizers {

    class SimpleToken(override val text: String, override val position: Int) : Token;

    fun loadKotoriTokenizer() : Tokenizer {
        return Tokenizer.createDefaultTokenizer()
    }

    fun loadSudachiTokenizer(
            systemDict:String = "../data/sudachi-dictionary-20200330/system_small.dic"
    ) : Tokenizer {
        val factory = com.worksap.nlp.sudachi.DictionaryFactory();
        val innerTokenizer = factory.create(null, "" +
                "{\"systemDict\":\"$systemDict\"}", true).create() ?: throw IllegalStateException();

        return object : Tokenizer {
            override fun tokenize(text: String): List<Token> =
                    innerTokenizer.tokenize(text).map { SimpleToken(it.surface(), it.begin()) }

            override fun toString(): String {
                return innerTokenizer.toString()
            }
        }
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
}