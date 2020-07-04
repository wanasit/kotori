package com.github.wanasit.kotori.core

import com.github.wanasit.kotori.*
import com.github.wanasit.kotori.optimized.PlainToken
import com.github.wanasit.kotori.optimized.tries.*

class LatticeBasedTokenizer<TermFeatures>(
        private val dictionary: Dictionary<TermFeatures>
) : Tokenizer<TermFeatures> {

    private val table: Array<Array<MinimalTermEntry>>
    private val dfa: DFA

    class MinimalTermEntry(
            val termID: TermID, val leftId: Int, val rightId: Int, val cost: Int)

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
            outputTable[it]?.map { termId ->
                val term = dictionary.terms[termId]!!
                MinimalTermEntry(termId, term.leftId, term.rightId, term.cost)
            }?.toTypedArray() ?: arrayOf()
        }
    }

    override fun tokenize(text: String): List<Token<TermFeatures>> {
        val lattice = Lattices.createLattice(dictionary.connection, text.length)
        val extractedUnknownTerms: MutableList<TermEntry<TermFeatures>> = ArrayList(128)

        for (i in text.indices) {
            if (!lattice.hasNodeEndingAtIndex(i)) {
                continue
            }

            var foundAnyTerm = false
            findTermsStartingAtIndex(text, i) { endIndex, term ->
                lattice.addNodeFromTermEntry(i, endIndex, term)
                foundAnyTerm = true
            }

            findUnknownTermsStartingAtIndex(text, i, force = !foundAnyTerm) {
                extractedUnknownTerms.add(it)
                lattice.addNodeFromUnknownTerm(i, -extractedUnknownTerms.size, it)
            }
        }

        val path = lattice.findPath()
        return path?.toTokens(text, dictionary, extractedUnknownTerms) ?: emptyList()
    }

    private inline fun findTermsStartingAtIndex(
            text: String, i: Int, action: (endIndex: Int, term: MinimalTermEntry) -> Unit) {
        var state = DFA.ROOT
        var index = i
        while (state != DFA.NONE && index < text.length) {
            state = dfa.nextState(state, text[index++].toInt())
            if (state != DFA.NONE) {
                table[state].forEach {
                    action(index, it)
                }
            }
        }
    }

    private inline fun findUnknownTermsStartingAtIndex(
            text: String, i: Int, force: Boolean, action: (term: TermEntry<TermFeatures>) -> Unit) {
        dictionary.unknownExtraction
                ?.extractUnknownTerms(text, i, force)?.forEach(action)
    }

    private fun Lattice.addNodeFromTermEntry(startIndex: Int, endIndex: Int, term: MinimalTermEntry) {
        this.addNode(LatticeNode(startIndex, endIndex,
                termID = term.termID, leftId = term.leftId, rightId = term.rightId, cost = term.cost))
    }

    private fun Lattice.addNodeFromUnknownTerm(startIndex: Int, assignedTermID: Int, term: TermEntry<*>) {
        this.addNode(LatticeNode(startIndex, startIndex + term.surfaceForm.length,
                termID = assignedTermID, leftId = term.leftId, rightId = term.rightId, cost = term.cost))
    }

    private fun List<LatticeNode>.toTokens(
            text: String,
            dictionary: Dictionary<TermFeatures>,
            extractedUnknownTerms: List<TermEntry<TermFeatures>>
    ) : List<Token<TermFeatures>> {

        return this.map {
            val termID = it.termID
            val tokenIndex = it.startIndex
            val tokenText = text.substring(it.startIndex, it.endIndex)
            val term = if (termID >= 0) {
                dictionary.terms[termID]!!
            } else {
                extractedUnknownTerms[-termID - 1]
            }

            PlainToken(tokenText, tokenIndex, term.features)
        }
    }
}