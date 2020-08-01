package com.github.wanasit.kotori.benchmark.tries

import com.github.wanasit.kotori.optimized.tries.DFA
import com.github.wanasit.kotori.optimized.tries.HashMapTrie
import com.github.wanasit.kotori.optimized.tries.State
import com.github.wanasit.kotori.optimized.tries.insert

typealias TerminalStateLookup = BooleanArray

fun createHashMapTrieAndIndexTerminalStates(terms: Collection<String>): Pair<HashMapTrie, TerminalStateLookup> {
    val hashMapTrie = HashMapTrie()
    val terminalStates = terms.map { hashMapTrie.insert(it) }

    val isTerminalStates = BooleanArray(hashMapTrie.size())
    terminalStates.forEach { isTerminalStates[it] = true }

    return hashMapTrie to isTerminalStates
}

class DFAWithTerminalState(
        val dfa: DFA,
        private val terminalStateLookup: TerminalStateLookup
) : DFA by dfa {

    fun isTerminalState(state: State) = terminalStateLookup[state]

    inline fun findTermsStartingAtIndex(charArray: CharArray, i: Int, action: (endIndex: Int) -> Unit) {
        var state = DFA.ROOT
        var index = i
        while (state != DFA.NONE && index < charArray.size) {
            state = dfa.nextState(state, charArray[index++].toInt())
            if (state != DFA.NONE && isTerminalState(state)) {
                action(index)
            }
        }
    }
}