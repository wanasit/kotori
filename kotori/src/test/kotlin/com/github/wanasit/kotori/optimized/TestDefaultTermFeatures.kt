package com.github.wanasit.kotori.optimized
import com.github.wanasit.kotori.optimized.DefaultTermFeatures.*
import com.github.wanasit.kotori.FakingTermDictionary
import com.github.wanasit.kotori.fakeTermDictionary
import org.junit.Assert.*
import org.junit.Test



class TestDefaultTermFeatures {

    fun FakingTermDictionary<DefaultTermFeatures>.term(
            surfaceForm: String, leftId: Int, rightId: Int, cost: Int, partOfSpeech: DefaultTermFeatures.PartOfSpeech
    ) {
        this.term(surfaceForm, leftId, rightId, cost, features = DefaultTermFeatures(
                partOfSpeech = partOfSpeech
        ))
    }

    @Test fun testReadWrite() {
        val file = createTempFile()

        val termEntries = fakeTermDictionary<DefaultTermFeatures> {
            term("そこで", 17, 17, 10, PartOfSpeech.NOUN)
            term("そこで", 18, 18, 100, PartOfSpeech.OTHER)
            term("Wow!", 152, 152, 100, PartOfSpeech.INTERJECTION)
        }.map { it.second }.toTypedArray()

        file.outputStream().use {
            DefaultTermFeatures.writeTermEntriesToOutput(it, termEntries)
        }

        val readTermEntries = file.inputStream().use {
            DefaultTermFeatures.readTermEntriesFromInputStream(it)
        }

        assertArrayEquals(termEntries, readTermEntries)
    }



}