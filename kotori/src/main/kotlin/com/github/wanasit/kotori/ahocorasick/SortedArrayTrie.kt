package com.github.wanasit.kotori.ahocorasick

import kotlin.math.max

private const val INIT_SIZE = 1024;
private const val NOT_USED = -1

class SortedArrayTrie(val trie: MutableDFA) : DFA {

    private var baseArray: IntArray
    private var nextArray: IntArray
    private val rootTransition: IntArray

    init {
        var nextIndex = 0
        baseArray = IntArray(trie.size())
        nextArray = IntArray(trie.size() * 2)

        val rootMaxTransition = trie.getTransition(0).maxBy { it.key }?.key ?: 0
        rootTransition = IntArray(rootMaxTransition + 1) { DFA.NONE }
        trie.getTransition(0).forEach {
            rootTransition[it.key] = it.value
        }


        IntRange(1, trie.size() - 1).forEach { state ->
            baseArray[state] = nextIndex
            val transitions = trie.getTransition(state)
            ensureNextAndCheckArraySize(nextIndex + (transitions.size * 2) + 1)

            transitions.entries.sortedBy { it.key }.forEach {
                nextArray[nextIndex++] = it.key
                nextArray[nextIndex++] = it.value
            }
            nextArray[nextIndex++] = 0
        }
    }

    override fun nextState(state: State, input: Int): State {
        if (state == 0) {
            return rootTransition.getOrElse(input) { DFA.NONE }
        }

        var index = baseArray[state]
        while (nextArray[index] != 0) {
            if (nextArray[index] == input) {
                return nextArray[index + 1]
            }

            index += 2
        }

        return DFA.NONE
    }

    override fun size(): Int {
        return baseArray.size
    }

    private fun ensureNextAndCheckArraySize(index: Int) {
        if (nextArray.size > index) {
            return
        }

        val newSize: Int = max(index + 1, nextArray.size * 2)
        val newNext = IntArray(newSize) { NOT_USED }
        nextArray.copyInto(newNext)
        nextArray = newNext
    }
}


