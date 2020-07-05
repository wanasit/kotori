package com.github.wanasit.kotori.dictionaries

import com.github.wanasit.kotori.mecab.MeCabLikeTermFeatures
import com.github.wanasit.kotori.optimized.DefaultTermFeatures
import com.github.wanasit.kotori.optimized.DefaultTermFeatures.*

object FeatureExtraction {

    val partOfSpeechLookupTable = mutableMapOf(
            "形容詞" to PartOfSpeech.ADJECTIVE,
            "連体詞" to PartOfSpeech.ADNOMINAL,
            "副詞" to PartOfSpeech.ADVERB,
            "助動詞" to PartOfSpeech.AUXILIARY,
            "接続詞" to PartOfSpeech.CONJUNCTION,
            "感動詞" to PartOfSpeech.INTERJECTION,
            "フィラー" to PartOfSpeech.INTERJECTION,
            "名詞" to PartOfSpeech.NOUN,
            "助詞" to PartOfSpeech.PARTICLE,
            "接頭詞" to PartOfSpeech.PREFIX,
            "接頭詞" to PartOfSpeech.PREFIX,
            "名詞,接尾" to PartOfSpeech.SUFFIX,
            "記号" to PartOfSpeech.SYMBOL,
            "動詞" to PartOfSpeech.VERB
    )

    fun transformFeatures(termFeatures: MeCabLikeTermFeatures) : DefaultTermFeatures {
        return DefaultTermFeatures(
                partOfSpeech = extractPartOfSpeech(termFeatures)
        )
    }

    fun extractPartOfSpeech(termFeatures: MeCabLikeTermFeatures) : PartOfSpeech {

        partOfSpeechLookupTable.lookup(
                termFeatures.partOfSpeech,
                termFeatures.partOfSpeechSubCategory1
        )?.run { return this }

        partOfSpeechLookupTable.lookup(
                termFeatures.partOfSpeech
        )?.run { return this }

        return PartOfSpeech.OTHER
    }


    private fun <T> Map<String, T>.lookup(vararg keys: String?): T? {
        val key = keys.map { it ?: "*" }.joinToString(",")
        return this[key]
    }
}