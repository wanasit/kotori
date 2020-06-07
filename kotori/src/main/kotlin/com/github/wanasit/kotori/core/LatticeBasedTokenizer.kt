package com.github.wanasit.kotori.core

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.TermID
import com.github.wanasit.kotori.Token
import com.github.wanasit.kotori.Tokenizer
import java.lang.IllegalStateException
import kotlin.math.min

class LatticeBasedToken(
        override val text: String,
        override val position: Int) : Token;

class LatticeBasedTokenizer(
        private val dictionary: Dictionary<*>
) : Tokenizer {

    override fun tokenize(text: String): List<Token> {
        val lattice = Lattice(text.length)
        for (i in text.indices) {
            val termIDs = findTermsStartingAtIndex(text, i)
            termIDs.forEach {
                val termEntry = dictionary.terms[it] ?: throw IllegalStateException()
                lattice.addNode(termEntry, i)
            }
        }

        for (i in text.indices) {
            val unknownTerms = dictionary.unknownExtraction
                    ?.extractUnknownTerms(text, i, !lattice.hasNodeStartAtIndex(i))
                    ?:emptyList()

            unknownTerms.forEach {
                lattice.addNode(it, i)
            }
        }

        val path = lattice.connectAndClose(dictionary.connection)
        return path?.map { LatticeBasedToken(it.termEntry.surfaceForm, it.location) } ?: emptyList()
    }

    // TODO More efficient matching approach
    //  e.g. prefix-tree, FST, or Automaton
    private val surfaceFormLookup: Map<String, List<TermID>>
    private val longestSurfaceForm: Int

    init {
        val lookupTable: MutableMap<String, MutableList<TermID>> = mutableMapOf()
        var longestSurfaceForm = 0;
        dictionary.terms.forEach {(termId, termEntry) ->
            longestSurfaceForm = maxOf(longestSurfaceForm, termEntry.surfaceForm.length)
            lookupTable.getOrPut(termEntry.surfaceForm, { mutableListOf() })
                    .add(termId)
        }

        this.surfaceFormLookup = lookupTable;
        this.longestSurfaceForm = longestSurfaceForm;
    }

    private fun findTermsStartingAtIndex(text: String, index: Int): List<TermID> {

        val result = mutableListOf<TermID>()
        val lastOffset = min(index+this.longestSurfaceForm, text.length)

        for (endOffset in index + 1..lastOffset) {
            result.addAll(surfaceFormLookup[text.substring(index, endOffset)] ?: listOf())
        }

        return result
    }
}