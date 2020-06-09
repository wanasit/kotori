package com.github.wanasit.kotori.ahocorasick

import kotlin.math.max

private const val INIT_SIZE = 1024;
private const val NOT_USED = -1

class TripleArrayTrie : MutableDFA {

    private var baseArray: IntArray = IntArray(INIT_SIZE) { NOT_USED }
    private var nextArray: IntArray = IntArray(INIT_SIZE) { NOT_USED }
    private var checkArray: IntArray = IntArray(INIT_SIZE) { NOT_USED }
    private var nonRootStateCount = 0;
    init {
        baseArray[0] = 0
    }

    private var transition: MutableMap<State, MutableMap<Input, State>> = mutableMapOf()

    override fun nextState(state: State, input: Int): State {
        val index = baseArray[state] + input
        if (index < checkArray.size && checkArray[index] == state) {
            return nextArray[index]
        }

        return DFA.NONE
    }

    override fun getTransition(state: State): Map<Input, State> {
        return transition[state] ?: mutableMapOf()
    }

    override fun put(vararg inputSeq: Input): State {
        var state = DFA.ROOT;
        for (i in inputSeq) {
            var base = baseArray[state]
            ensureNextAndCheckArraySize(base + i)
            if (checkArray[base + i] == state) {
                state = nextArray[base + i]
                continue
            }

            val nextState = registerNewState()
            if (checkArray[base + i] != NOT_USED) {
                base = relocate(state, i)
            }

            transition.getOrPut(state) { mutableMapOf() }[i] = nextState

            nextArray[base + i] = nextState
            checkArray[base + i] = state
            state = nextState
        }

        return state
    }

    override fun size(): Int {
        return nonRootStateCount + 1;
    }

    private fun relocate(state: State, newInput: Input) : Int {
        val currentBase = baseArray[state]
        val currentInputs = transition[state]?.keys?.toMutableList()?: mutableListOf()
        currentInputs.add(newInput)

        var collision = true
        var newBase = 0
        while (collision) {
            collision = false

            for (i in currentInputs) {
                ensureNextAndCheckArraySize(newBase + i)
                if (checkArray[newBase + i] != NOT_USED) {
                    collision = true
                    newBase += 1
                    break
                }
            }
        }

        for (i in currentInputs) {
            if (i != newInput) {
                checkArray[newBase + i] = state
                nextArray[newBase + i] = nextArray[currentBase + i]
                checkArray[currentBase + i] = NOT_USED;
            }
        }

        baseArray[state] = newBase
        return newBase
    }

    private fun registerNewState() : Int{
        nonRootStateCount += 1
        val nextState = nonRootStateCount
        ensureBaseArraySize(nextState)
        baseArray[nextState] = nextState
        return nextState
    }

    private fun ensureBaseArraySize(index: Int) {
        if (baseArray.size > index) {
            return
        }

        val newSize: Int = max(index + 1, baseArray.size * 2)
        val newTable = IntArray(newSize) { NOT_USED }
        baseArray.copyInto(newTable)
        baseArray = newTable
    }

    private fun ensureNextAndCheckArraySize(index: Int) {
        if (nextArray.size > index) {
            return
        }

        val newSize: Int = max(index + 1, nextArray.size * 2)
        val newNext = IntArray(newSize) { NOT_USED }
        val newCheck = IntArray(newSize) { NOT_USED }
        nextArray.copyInto(newNext)
        checkArray.copyInto(newCheck)
        nextArray = newNext
        checkArray = newCheck
    }
}


