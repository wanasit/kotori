package com.github.wanasit.kotori.core

import com.github.wanasit.kotori.*
import com.github.wanasit.kotori.ahocorasick.AhoCorasickPatternMatcher
import com.github.wanasit.kotori.ahocorasick.PatternMatchingStrategy

class LatticeBasedToken(
        override val text: String,
        override val position: Int) : Token;

class LatticeBasedTokenizer(
        private val dictionary: Dictionary<*>,
        private val matchingStrategy: PatternMatchingStrategy<TermEntry> =
                AhoCorasickPatternMatcher(dictionary.terms.map { it.second.surfaceForm to it.second })
) : Tokenizer {

    override fun tokenize(text: String): List<Token> {
        val lattice = Lattice(text.length)
        val matcher = matchingStrategy.matcher()

        text.forEachIndexed {index, c ->
            val terms = matcher.processNextChar(c);
            terms.forEach {
                val endIndex = index + 1
                val startIndex = endIndex - it.surfaceForm.length
                lattice.addNode(it, startIndex, endIndex)
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
}