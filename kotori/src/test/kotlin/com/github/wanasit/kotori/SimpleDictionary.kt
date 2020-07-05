package com.github.wanasit.kotori

import com.github.wanasit.kotori.optimized.PlainTermEntry
import com.github.wanasit.kotori.optimized.PlainToken

typealias WordType = Int

/**
 * A utility function for writing term dictionary
 *
 * simpleTermDictionary {
 *     term("そこで", CONJ, 10)
 *     term("そこ", NOUN, 40)
 *     ...
 * }
 */
fun <F> fakeTermDictionary(init: FakingTermDictionary<F>.() -> Unit) : TermDictionary<F> {
    val termDictionary = FakingTermDictionary<F>()
    termDictionary.init()
    return termDictionary;
}

fun fakeTermDictionaryWithoutFeature(init: FakingTermDictionaryWithEmptyFeatures.() -> Unit): TermDictionary<PlainToken.EmptyFeatures> {
    val termDictionary = FakingTermDictionaryWithEmptyFeatures()
    termDictionary.init()
    return termDictionary;
}

/**
 * A utility function for writing a connection cost as a table
 *
 * connectionTable {
 *   header(     END,    NOUN,   VERB,   ADJ,    CONJ)
 *     row(BEGIN,  0,      10,     10,     0,      10)
 *     row(NOUN,   10,     10,     40,     10,      0)
 *     ...
 * }
 */
fun connectionTable(init: FakeConnectionTable.() -> Unit) : ConnectionCost {
    val connectionCost = FakeConnectionTable()
    connectionCost.init()
    return connectionCost;
}

open class FakingTermDictionaryWithEmptyFeatures : FakingTermDictionary<PlainToken.EmptyFeatures>() {
    fun term(surfaceForm: String, wordType: WordType, cost: Int) {
        term(surfaceForm, wordType, cost, PlainToken.EmptyFeatures())
    }
}

open class FakingTermDictionary<F> : TermDictionary<F> {
    private val entries: MutableList<TermEntry<F>> = mutableListOf();

    override fun get(id: Int): TermEntry<F>? {
        return entries.get(id);
    }

    override fun iterator(): Iterator<Pair<Int, TermEntry<F>>> {
        return entries.mapIndexed { i, e -> i to e}.iterator()
    }

    fun term(surfaceForm: String, leftId: Int, rightId: Int, cost: Int, features: F) {
        entries.add(PlainTermEntry(
                surfaceForm = surfaceForm,
                leftId = leftId,
                rightId = rightId,
                cost = cost,
                features = features
        ))
    }

    fun term(surfaceForm: String, wordType: WordType, cost: Int, features: F) {
        term(surfaceForm, wordType, wordType, cost, features)
    }

    val NOUN: WordType = 1
    val VERB: WordType = 2
    val ADJ: WordType = 3
    val CONJ: WordType = 4
}

class FakeConnectionTable : ConnectionCost {

    val BEGIN: WordType = 0
    val END: WordType = 0
    val NOUN: WordType = 1
    val VERB: WordType = 2
    val ADJ: WordType = 3
    val CONJ: WordType = 4

    private val connection: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()

    override fun lookup(fromRightId: Int, toLeftId: Int): Int {
        return connection[fromRightId to toLeftId] ?: Int.MAX_VALUE
    }

    private var toTypes: List<WordType>? = null

    fun header(vararg headerRow: WordType) {
        toTypes = headerRow.toList()
    }

    fun row(fromType: WordType, vararg costInfo: Int) {
        check(toTypes != null)
        check(costInfo.size == toTypes?.size)
        toTypes?.zip(costInfo.toList())?.forEach { (toType, cost) ->
            connection[fromType to toType] = cost
        }
    }
}

