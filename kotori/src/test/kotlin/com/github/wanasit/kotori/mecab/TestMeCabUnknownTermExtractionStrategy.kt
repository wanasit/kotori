package com.github.wanasit.kotori.mecab

import kotlin.test.Test
import kotlin.test.assertTrue

class TestMeCabUnknownTermExtractionStrategy {

    private val defaultUnknownTermStrategy = MeCabDictionary.readFromResource().unknownExtraction
            ?: throw IllegalStateException("Couldn't read default MeCab's dictionary from resource")

    @Test fun testNormalExtraction() {
        val text = "TFSって何？";

        assertTrue(defaultUnknownTermStrategy.extractUnknownTerms(
                text, text.indexOf("TFS"), false).toList().isNotEmpty())

        assertTrue(defaultUnknownTermStrategy.extractUnknownTerms(
                text, text.indexOf("っ"), false).toList().isEmpty())

        assertTrue(defaultUnknownTermStrategy.extractUnknownTerms(
                text, text.indexOf("て"), false).toList().isEmpty())

        assertTrue(defaultUnknownTermStrategy.extractUnknownTerms(
                text, text.indexOf("何"), false).toList().isEmpty())

        assertTrue(defaultUnknownTermStrategy.extractUnknownTerms(
                text, text.indexOf("？"), false).toList().isNotEmpty())
    }

    @Test fun testForcingExtraction() {
        val text = "TFSって何？";

        assertTrue(defaultUnknownTermStrategy.extractUnknownTerms(
                text, text.indexOf("っ"), true).toList().isNotEmpty())

        assertTrue(defaultUnknownTermStrategy.extractUnknownTerms(
                text, text.indexOf("て"), true).toList().isNotEmpty())

        assertTrue(defaultUnknownTermStrategy.extractUnknownTerms(
                text, text.indexOf("何"), true).toList().isNotEmpty())
    }
}