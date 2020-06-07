package com.github.wanasit.kotori.ahocorasick

interface MutableDFA : DFA {
    fun put(vararg inputSeq: Input) : State;
    fun getTransition(state: State) : Map<Input, State>
}

class HashMapTrie : MutableDFA {
    private var nonRootStateCount = 0;
    private val transitionTable: MutableList<MutableMap<Input, State>> = mutableListOf();
    init {
        transitionTable.add(mutableMapOf())
    }

    override fun nextState(state: State, input: Int): State {
        return transitionTable[state][input] ?: DFA.NONE
    }

    override fun put(vararg inputSeq: Input): State {
        var state = DFA.ROOT;
        for (i in inputSeq) {
            var nextState = transitionTable[state].get(i) ?: DFA.NONE
            if (nextState == DFA.NONE) {
                nonRootStateCount += 1;

                transitionTable.add(mutableMapOf())
                transitionTable[state].put(i, nonRootStateCount)

                nextState = nonRootStateCount
            }

            state = nextState;
        }

        return state
    }

    override fun getTransition(state: State): Map<Input, State> {
        return transitionTable[state]
    }

    override fun size(): Int {
        return nonRootStateCount + 1;
    }
}


