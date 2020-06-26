package com.github.wanasit.kotori.sudachi.dictionary

import com.github.wanasit.kotori.mecab.MeCabTermEntry
import com.github.wanasit.kotori.mecab.MeCabUnknownTermExtractionStrategy
import com.github.wanasit.kotori.optimized.dictionary.UnknownTermExtractionByCharacterCategory
import com.worksap.nlp.sudachi.Settings

object SudachiUnknownTermExtraction {
    fun readDefaultMeCabUnknownTermExtraction(): UnknownTermExtractionByCharacterCategory<MeCabTermEntry> {
        val charDefinitionInputStream = Settings::class.java.classLoader.getResourceAsStream("char.def")!!
        val unknownDefinitionInputStream = Settings::class.java.classLoader.getResourceAsStream("unk.def")!!

        return MeCabUnknownTermExtractionStrategy.readFromFileInputStreams(
                unknownDefinitionInputStream, charDefinitionInputStream, Charsets.UTF_8)
    }
}