package com.github.wanasit.kotori.optimized.unknown

import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.mecab.MeCabDictionary
import com.github.wanasit.kotori.mecab.MeCabTermFeatures
import com.github.wanasit.kotori.mecab.readFromResource
import com.github.wanasit.kotori.optimized.PlainTermEntry
import com.github.wanasit.kotori.optimized.PlainToken
import com.github.wanasit.kotori.optimized.unknown.UnknownTermExtractionByCharacterCategory.ExtractedUnknownTermEntry
import org.junit.Assert
import org.junit.Test


class TestUnknownTermExtractionByCharacterCategory {

    val MECAB_UNKWOWN_TERM_EXTRACTION: UnknownTermExtractionByCharacterCategory<MeCabTermFeatures> =
            UnknownTermExtractionByCharacterCategory.copyOf(
                    MeCabDictionary.readFromResource().unknownExtraction as UnknownTermExtractionByCharacterCategory<MeCabTermFeatures>
            ) { it }

    @Test
    fun testMecabExtractionCopy() {
        val unknownTerms: List<ExtractedUnknownTermEntry<MeCabTermFeatures>> = MECAB_UNKWOWN_TERM_EXTRACTION
                .extractUnknownTerms("GoogleがAndroid向け点字キーボードを発表", 0, false).toList()

        Assert.assertTrue(unknownTerms.isNotEmpty())
        Assert.assertTrue(unknownTerms.any { it.surfaceForm == "Google" && it.term == "Google" })
        Assert.assertTrue(unknownTerms.any { it.unknownDictionaryEntry.surfaceForm == "ALPHA" })
    }

    @Test
    fun testCreateEmptyExtraction() {

        val unknownTermExtraction: UnknownTermExtractionByCharacterCategory<PlainToken.EmptyFeatures> =
                UnknownTermExtractionByCharacterCategory.fromUnoptimizedMapping(mapOf(), mapOf(), mapOf())

        val unknownTerms: Iterable<*> = unknownTermExtraction.extractUnknownTerms("some string", 0, false)
        Assert.assertTrue(unknownTerms.toList().isEmpty())
    }
}