package com.github.wanasit.kotori.optimized.tries




typealias State = Int;
typealias Input = Int;
typealias InputSequence = IntArray

/**
 * Deterministic Finite Automata (DFA)
 */
interface DFA {
    fun nextState(state: State, input: Int): State
    fun size(): Int

    companion object {
        const val ROOT: State = 0
        const val NONE: State = -1
    }
}

interface MutableDFA : DFA {
    fun getTransition(state: State) : Map<Input, State>
    fun nextOrPutState(state: State, input: Input) : State
}


fun DFA.states() : Collection<State> {
    return IntRange(0, size() - 1).toList()
}

fun DFA.nonRootStates() : Collection<State> {
    return IntRange(1, size() - 1).toList()
}

fun DFA.get(sequence: String): State {
    var state = DFA.ROOT
    for (i in sequence.chars()) {
        state = this.nextState(state, i)
        if (state == DFA.NONE) {
            return state
        }
    }

    return state
}

fun MutableDFA.insert(sequence: String): State {
    val inputSeq: InputSequence = sequence.chars().toArray()
    return this.insert(inputSeq)
}

fun MutableDFA.insert(inputSequence: InputSequence): State {
    var state = DFA.ROOT;
    for (i in inputSequence) {
        state = this.nextOrPutState(state, i)
    }

    return state
}
