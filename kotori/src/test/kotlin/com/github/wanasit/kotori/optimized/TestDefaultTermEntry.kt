package com.github.wanasit.kotori.optimized

import org.junit.Assert
import org.junit.Test

class TestDefaultTermEntry {

    @Test
    fun testBasicCreationAndSerialization() {
        val terms = listOf(
                DefaultTermEntry("そこで", 1, 10, DefaultTermFeatures()),
                DefaultTermEntry("で", 12, 10, DefaultTermFeatures(
                        DefaultTermFeatures.PartOfSpeech.VERB)),
                DefaultTermEntry("そこで", 17, 10,
                        DefaultTermFeatures(DefaultTermFeatures.PartOfSpeech.NOUN)),
                DefaultTermEntry("そこで", 18, 100,
                        DefaultTermFeatures(DefaultTermFeatures.PartOfSpeech.OTHER)),
                DefaultTermEntry("Wow!", 152, 100,
                        DefaultTermFeatures(DefaultTermFeatures.PartOfSpeech.INTERJECTION))
        ).toTypedArray()

        val file = createTempFile()
        file.deleteOnExit()
        file.outputStream().use {
            DefaultTermEntry.writeToOutput(it, terms);
        }

        val readTerms = file.inputStream().use {
            DefaultTermEntry.readFromInputStream(it)
        }

        Assert.assertArrayEquals(terms, readTerms)
    }
}