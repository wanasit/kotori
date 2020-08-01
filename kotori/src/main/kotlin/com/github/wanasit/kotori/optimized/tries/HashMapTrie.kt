package com.github.wanasit.kotori.optimized.tries


class HashMapTrie : MutableDFA {
    private var nonRootStateCount = 0;
    private val transitionTable: MutableList<MutableMap<Input, State>> = mutableListOf();
    init {
        transitionTable.add(mutableMapOf())
    }

    override fun nextState(state: State, input: Int): State {
        return transitionTable[state][input] ?: DFA.NONE
    }

    override fun nextOrPutState(state: State, input: Input): State {
        var nextState = transitionTable[state][input] ?: DFA.NONE
        if (nextState == DFA.NONE) {
            nonRootStateCount += 1;

            transitionTable.add(hashMapOf())
            transitionTable[state].put(input, nonRootStateCount)

            nextState = nonRootStateCount
        }

        return nextState;
    }

    override fun getTransition(state: State): Map<Input, State> {
        return transitionTable[state]
    }

    override fun size(): Int {
        return nonRootStateCount + 1;
    }
}

