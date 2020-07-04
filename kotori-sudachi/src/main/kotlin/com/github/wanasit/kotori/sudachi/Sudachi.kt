package com.github.wanasit.kotori.sudachi

import com.github.wanasit.kotori.Token
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.mecab.MeCabTermFeatures
import com.github.wanasit.kotori.optimized.PlainToken
import com.github.wanasit.kotori.sudachi.dictionary.SudachiDictionary
import com.github.wanasit.kotori.sudachi.dictionary.SudachiUnknownTermExtraction
import java.lang.IllegalStateException


object Sudachi {

    fun loadKotoriTokenizerWithSudachiDict(
            systemDict:String = "../data/sudachi-dictionary/system_small.dic"
    ) : Tokenizer<MeCabTermFeatures> {

        val unknownTermExtraction = SudachiUnknownTermExtraction.readDefaultMeCabUnknownTermExtraction()
        val dictionary = SudachiDictionary.readSystemDictionary(systemDict, unknownTermExtraction)

        return Tokenizer.create(dictionary)
    }

    fun loadSudachiTokenizer(
            systemDict:String = "../data/sudachi-dictionary/system_small.dic"
    ) : Tokenizer<MeCabTermFeatures> {
        val factory = com.worksap.nlp.sudachi.DictionaryFactory()
        val settings = """
                    {
                        "systemDict" : "$systemDict",
                        "oovProviderPlugin" : [
                            { "class" : "com.worksap.nlp.sudachi.MeCabOovProviderPlugin" }
                        ],
                        "inputTextPlugin": [],
                        "pathRewritePlugin" : []
                    }
                """.trimIndent();


        val innerTokenizer = factory.create(null, settings, true).create()
                ?: throw IllegalStateException();

        return object : Tokenizer<MeCabTermFeatures> {
            override fun tokenize(text: String): List<Token<MeCabTermFeatures>> =
                    innerTokenizer.tokenize(text).map { PlainToken(it.surface(), it.begin(),
                            MeCabTermFeatures(partOfSpeech = it.partOfSpeech().first())
                    ) }

            override fun toString(): String {
                return innerTokenizer.toString()
            }
        }
    }
}




