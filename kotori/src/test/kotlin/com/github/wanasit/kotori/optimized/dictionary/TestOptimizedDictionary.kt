package com.github.wanasit.kotori.optimized.dictionary

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.TermDictionary
import com.github.wanasit.kotori.connectionTable
import com.github.wanasit.kotori.simpleTermDictionary
import com.github.wanasit.kotori.utils.IOUtils
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class TestOptimizedDictionary {

    val TESTING_TERM_DICT = simpleTermDictionary {
        term("そこで", CONJ, 10)
        term("そこ", NOUN, 40)
        term("で", VERB, 40)
    }

    val TESTING_CONNECTION_COST = connectionTable {
        header(     END,    NOUN,   VERB,   ADJ,    CONJ)
        row(BEGIN,  0,      10,     10,     0,      10)
        row(NOUN,   10,     10,     40,     10,      0)
        row(VERB,   10,     10,     10,     0,      10)
        row(ADJ,    10,     10,     10,     10,     10)
        row(CONJ,   0,      10,     10,     0,      10)
    }

    @Test fun testCopyingDictionary() {
        val dictionary = Dictionary(TESTING_TERM_DICT, TESTING_CONNECTION_COST)
        val optimizedDictionary = OptimizedDictionary.copyOf(dictionary)

        assertTermEntriesEqual(dictionary.terms, optimizedDictionary.terms)
        assertConnectionCostEquals(dictionary, optimizedDictionary)
    }

    @Test fun testDictionarySerialization() {
        val tempFile = createTempFile()
        val dictionary = Dictionary(TESTING_TERM_DICT, TESTING_CONNECTION_COST)
        val optimizedDictionary = OptimizedDictionary.copyOf(dictionary)

        tempFile.outputStream().use {
            optimizedDictionary.writeToOutputStream(it)
        }

        val readDictionary = tempFile.inputStream().use {
            OptimizedDictionary.readFromInputStream(it)
        }

        assertTermEntriesEqual(optimizedDictionary.terms, readDictionary.terms)
        assertConnectionCostEquals(optimizedDictionary, readDictionary)
    }

    @Test fun testDictionarySerializationWithUnknownTermExtraction() {
        val tempFile = createTempFile()

        val unknownExtraction = UnknownTermExtractionByCharacterCategory
                .fromUnoptimizedMapping(
                        mapOf(
                                ' ' to listOf(1)
                        ),
                        mapOf(
                                0 to CharCategoryDefinition(false, true, 0),
                                1 to CharCategoryDefinition(false, true, 0)
                        ),
                        emptyMap())

        val dictionary = Dictionary(TESTING_TERM_DICT, TESTING_CONNECTION_COST, unknownExtraction)
        val optimizedDictionary = OptimizedDictionary.copyOf(dictionary)

        tempFile.outputStream().use {
            optimizedDictionary.writeToOutputStream(it)
        }

        val readDictionary = tempFile.inputStream().use {
            OptimizedDictionary.readFromInputStream(it)
        }

        assertTermEntriesEqual(optimizedDictionary.terms, readDictionary.terms)
        assertConnectionCostEquals(optimizedDictionary, readDictionary)

        assertNotNull(readDictionary.unknownExtraction)
    }


    private fun assertTermEntriesEqual(expectedTerms: TermDictionary<*>, actualTerms: TermDictionary<*>) {
        val termList = expectedTerms.map { it.second }.sortedBy { it.surfaceForm }
        val optimalTermList = actualTerms.map { it.second }.sortedBy { it.surfaceForm }
        assertEquals(termList.size, optimalTermList.size)
        assertEquals(termList.map { it.surfaceForm }, optimalTermList.map { it.surfaceForm })
        assertEquals(termList.map { it.leftId }, optimalTermList.map { it.leftId })
        assertEquals(termList.map { it.rightId }, optimalTermList.map { it.rightId })
        assertEquals(termList.map { it.cost }, optimalTermList.map { it.cost })
    }

    private fun assertConnectionCostEquals(expectDictionary: Dictionary<*>, actualDictionary: Dictionary<*>) {
        val maxFromId = expectDictionary.terms.map { it.second.leftId }.max() ?: 0
        val maxToId = expectDictionary.terms.map { it.second.rightId }.max() ?: 0
        for (i in 0..maxFromId) {
            for (j in 0..maxToId) {
                assertEquals(
                        expectDictionary.connection.lookup(i, j),
                        actualDictionary.connection.lookup(i, j)
                )
            }
        }
    }



}