package com.github.wanasit.kotori

import com.github.wanasit.kotori.utils.checkArgument

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
fun simpleTermDictionary(init: SimpleTermDictionary.() -> Unit) : TermDictionary<TermEntry> {
    val termDictionary = SimpleTermDictionary()
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
fun connectionTable(init: SimpleConnectionTable.() -> Unit) : ConnectionCost {
    val connectionCost = SimpleConnectionTable()
    connectionCost.init()
    return connectionCost;
}

class SimpleTermDictionary : TermDictionary<TermEntry> {
    private val entries: MutableList<TermEntry> = mutableListOf();

    override fun get(id: Int): TermEntry? {
        return entries.get(id);
    }

    override fun iterator(): Iterator<Pair<Int, TermEntry>> {
        return entries.mapIndexed { i, e -> i to e}.iterator()
    }

    fun term(surfaceForm: String, wordType: WordType, cost: Int) {
        entries.add(object : TermEntry {
            override val surfaceForm = surfaceForm
            override val leftId = wordType
            override val rightId = wordType
            override val cost = cost
        })
    }

    val NOUN: WordType = 1
    val VERB: WordType = 2
    val ADJ: WordType = 3
    val CONJ: WordType = 4
}

class SimpleConnectionTable() : ConnectionCost {

    val BEGIN: WordType = 0
    val END: WordType = 0
    val NOUN: WordType = 1
    val VERB: WordType = 2
    val ADJ: WordType = 3
    val CONJ: WordType = 4

    private val connection: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()

    override fun lookup(fromRightId: Int, toLeftId: Int): Int? {
        return connection[fromRightId to toLeftId]
    }

    private var toTypes: List<WordType>? = null

    fun header(vararg headerRow: WordType) {
        toTypes = headerRow.toList()
    }

    fun row(fromType: WordType, vararg costInfo: Int) {
        checkArgument(toTypes != null)
        checkArgument(costInfo.size == toTypes?.size)
        toTypes?.zip(costInfo.toList())?.forEach { (toType, cost) ->
            connection[fromType to toType] = cost
        }
    }
}

