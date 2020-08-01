package com.github.wanasit.kotori.optimized.tries

import java.util.*
import kotlin.math.max

class TransitionArrayTrie(baseDFA: MutableDFA) : DFA {

    private var baseArray: IntArray
    private var transitionArray: IntArray
    private val rootTransition: IntArray

    init {
        var nextIndex = 0
        baseArray = IntArray(baseDFA.size())
        transitionArray = IntArray(baseDFA.size() * 2)

        val rootMaxTransition = baseDFA.getTransition(0).maxBy { it.key }?.key ?: 0
        rootTransition = IntArray(rootMaxTransition + 1) { DFA.NONE }
        baseDFA.getTransition(0).forEach {
            rootTransition[it.key] = it.value
        }

        baseDFA.nonRootStates().forEach { state ->
            val transitions = baseDFA.getTransition(state)
            if (transitions.isEmpty()) {
                baseArray[state] = -1
            } else {
                val transitionBlockIndex = nextIndex
                nextIndex += (transitions.size * 2) + 1

                ensureNextAndCheckArraySize(nextIndex + (transitions.size * 2) + 1)

                baseArray[state] = transitionBlockIndex
                transitionArray[transitionBlockIndex] = transitions.size

                transitions.entries.sortedBy { it.key }.forEachIndexed { i, transition ->
                    transitionArray[transitionBlockIndex + 1 + i] = transition.key
                    transitionArray[transitionBlockIndex + 1 + i + transitions.size] = transition.value
                }
            }
        }
    }

    override fun nextState(state: State, input: Int): State {
        if (state == 0) {
            return rootTransition.getOrElse(input) { DFA.NONE }
        }

        val transitionBlockIndex = baseArray[state]
        if (transitionBlockIndex < 0) {
            return DFA.NONE
        }

        val transitionBlockSize = transitionArray[transitionBlockIndex]
        val transitionKeyFromIndex = transitionBlockIndex + 1
        val transitionKeyToIndex = transitionBlockIndex + transitionBlockSize + 1
        val transitionKeyIndex = Arrays.binarySearch(
                transitionArray, transitionKeyFromIndex, transitionKeyToIndex, input)

        if (transitionKeyIndex < 0) {
            return DFA.NONE
        }

        val transitionValueIndex = transitionKeyIndex + transitionBlockSize
        return transitionArray[transitionValueIndex]
    }

    override fun size(): Int {
        return baseArray.size
    }

    private fun ensureNextAndCheckArraySize(index: Int) {
        if (transitionArray.size > index) {
            return
        }

        val newSize: Int = max(index + 1, transitionArray.size * 2)
        val newNext = IntArray(newSize)
        transitionArray.copyInto(newNext)
        transitionArray = newNext
    }
}


