package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.connectionTable
import com.github.wanasit.kotori.fakeTermDictionaryWithoutFeature
import com.github.wanasit.kotori.optimized.unknown.UnknownTermExtractionByCharacterCategory
import com.github.wanasit.kotori.utils.asEntries
import com.github.wanasit.kotori.utils.termEntries
import com.github.wanasit.kotori.utils.withoutFeatures
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class TestDefaultDictionary {

    @Test
    fun testBasicCreationAndSerialization() {
        val terms = fakeTermDictionaryWithoutFeature {
            term("そこで", CONJ, 10)
            term("そこ", NOUN, 40)
            term("で", VERB, 40)
            term("で", ADJ, 10)
            term("はなし", NOUN, 40)
            term("は", VERB, 10)
            term("なし", NOUN, 40)
            term("終わり", NOUN, 40)
            term("になった", VERB, 40)
            term("に", ADJ, 10)
            term("なった", VERB, 40)
        }.asEntries

        val connectionCost = connectionTable {
            header(     END,    NOUN,   VERB,   ADJ,    CONJ)
            row(BEGIN,  0,      10,     10,     0,      10)
            row(NOUN,   10,     10,     40,     10,      0)
            row(VERB,   10,     10,     10,     0,      10)
            row(ADJ,    10,     10,     10,     10,     10)
            row(CONJ,   0,      10,     10,     0,      10)
        }

        val unknownExtraction: UnknownTermExtractionByCharacterCategory<DefaultTermFeatures> =
                UnknownTermExtractionByCharacterCategory.fromUnoptimizedMapping(emptyMap(), emptyMap(), emptyMap())

        val dictionary = DefaultDictionary(
                terms = PlainTermDictionary.copyOf(terms) { PlainTermEntry(it, DefaultTermFeatures()) },
                unknownExtraction = unknownExtraction,
                connection = PlainConnectionCostTable.copyOf(terms, connectionCost)
        )

        val file = createTempFile()
        file.deleteOnExit()
        file.outputStream().use {
            DefaultDictionary.writeToOutputStream(it, dictionary);
        }

        val readDictionary = file.inputStream().use {
            DefaultDictionary.readFromInputStream(it)
        }

        assertEquals(
                terms.map { it.withoutFeatures() },
                readDictionary.termEntries.map { it.withoutFeatures() })
        assertEquals(connectionCost.lookup(1, 1), readDictionary.connection.lookup(1 , 1))
        assertEquals(connectionCost.lookup(3, 1), readDictionary.connection.lookup(3 , 1))
    }
}