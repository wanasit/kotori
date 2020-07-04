package com.github.wanasit.kotori.core

import com.github.wanasit.kotori.*
import com.github.wanasit.kotori.optimized.DefaultToken
import com.github.wanasit.kotori.optimized.tries.*

class LatticeBasedTokenizer<TermFeatures>(
        private val dictionary: Dictionary<TermFeatures>
) : Tokenizer<TermFeatures> {

    private val table: Array<Array<TermEntry<TermFeatures>>>
    private val dfa: DFA

    init {
        val outputTable: MutableMap<State, MutableSet<TermID>> = mutableMapOf()
        val trie = HashMapTrie()
        dictionary.terms.forEach{(termId, term) ->
            val state = trie.insert(term.surfaceForm)
            outputTable.getOrPut(state, { mutableSetOf() })
                    .add(termId)
        }

        dfa = TransitionArrayTrie(trie)
        table = Array(dfa.size()) {
            outputTable[it]?.map { dictionary.terms[it]!! }?.toTypedArray() ?: arrayOf()
        }
    }

    override fun tokenize(text: String): List<Token<TermFeatures>> {
        val lattice = Lattices.createLattice(dictionary.connection, text.length)

        for (i in text.indices) {
            if (!lattice.hasNodeEndingAtIndex(i)) {
                continue
            }
            val found = processTerms(lattice, text, i)
            val unknownTerms = dictionary.unknownExtraction
                    ?.extractUnknownTerms(text, i, !found)
                    ?:emptyList()

            unknownTerms.forEach {
                lattice.addNode(it, i)
            }
        }

        val path = lattice.findPath()
        return path?.map { DefaultToken<TermFeatures>(it.termEntry.surfaceForm, it.location) } ?: emptyList()
    }

    private fun processTerms(lattice: Lattice, text: String, i: Int) : Boolean {

        var found = false
        var state = DFA.ROOT
        var index = i
        while (state != DFA.NONE && index < text.length) {
            state = dfa.nextState(state, text[index++].toInt())
            if (state != DFA.NONE) {
                table[state].forEach {
                    lattice.addNode(it, i, index)
                    found = true
                }
            }
        }

        return found
    }
}