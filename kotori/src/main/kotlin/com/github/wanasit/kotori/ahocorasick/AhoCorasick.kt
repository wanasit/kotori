package com.github.wanasit.kotori.ahocorasick

import java.util.*

typealias State = Int;
typealias Input = Int;
typealias InputSequence = IntArray

/**
 * Deterministic Finite Automata (DFA)
 */
interface DFA {
    fun nextState(state: State, input: Int): State;
    fun size(): Int;

    companion object {
        const val ROOT: State = 0;
        const val NONE: State = -1;
    }
}

interface MutableDFA : DFA {
    fun put(vararg inputSeq: Input) : State;
    fun getTransition(state: State) : Map<Input, State>
}

class AhoCorasick(
        val dfa: DFA,
        private val fallbacks: IntArray
) : DFA {

    override fun nextState(state: State, input: Int): State {
        var currentState = state
        var nextState = dfa.nextState(currentState, input)

        while (nextState == DFA.NONE) {
            if (currentState == DFA.ROOT) {
                nextState = DFA.ROOT
                break
            }

            currentState = fallbacks[currentState]
            nextState = dfa.nextState(currentState, input)
        }
        return nextState
    }

    override fun size(): Int {
        return fallbacks.size;
    }

    fun fallback(state: State) : Int {
        return fallbacks[state]
    }

    class Builder {

        private val mutableDFA = HashMapTrie();

        fun put(vararg inputSeq: Input): State {
            return mutableDFA.put(*inputSeq);
        }

        fun build() : AhoCorasick{
            val fallbacks = IntArray(mutableDFA.size()) { DFA.ROOT }

            val queue: Queue<State> = LinkedList()
            queue.addAll(mutableDFA.getTransition(DFA.ROOT).values)

            while (queue.isNotEmpty()) {
                val currentState = queue.poll();

                mutableDFA.getTransition(currentState).forEach { input, nextState ->

                    var currentFallback = fallbacks[currentState]
                    var nextFallback = mutableDFA.nextState(currentFallback, input)

                    while (nextFallback == DFA.NONE) {
                        if (currentFallback == DFA.ROOT) {
                            nextFallback = DFA.ROOT
                            break
                        }

                        currentFallback = fallbacks[currentFallback]
                        nextFallback = mutableDFA.nextState(currentFallback, input)
                    }

                    fallbacks[nextState] = nextFallback
                    queue.add(nextState)
                }
            }

            return AhoCorasick(SortedArrayTrie(mutableDFA), fallbacks);
        }
    }
}
