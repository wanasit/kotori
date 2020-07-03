package com.github.wanasit.kotori.sudachi.dictionary

import com.github.wanasit.kotori.*
import com.github.wanasit.kotori.optimized.dictionary.ConnectionCostArray
import com.worksap.nlp.sudachi.dictionary.BinaryDictionary
import com.worksap.nlp.sudachi.dictionary.Grammar
import com.worksap.nlp.sudachi.dictionary.Lexicon


object SudachiDictionary {

    fun readSystemDictionary(
            dictionaryFile: String, unknownExtraction: UnknownTermExtractionStrategy? = null): Dictionary<SudachiTermEntry> {

        val dictionary = BinaryDictionary.readSystemDictionary(dictionaryFile)
        val terms = SudachiTermEntry.fromLexicon(dictionary.lexicon)
        val termDictionary = TermEntryArray(terms.toTypedArray())

        val maxLeftId = terms.map { it.leftId }.max() ?: 0
        val maxRightId = terms.map { it.rightId }.max() ?: 0
        val termConnection = connectionCostFromGrammar(dictionary.grammar, maxLeftId, maxRightId)

        return Dictionary(
                termDictionary,
                termConnection,
                unknownExtraction)
    }


    private fun connectionCostFromGrammar(grammar: Grammar, maxLeftId: Int, maxRightId: Int) : ConnectionCost {
        return ConnectionCostArray.copyOf(maxLeftId + 1, maxRightId + 1) {leftId, rightId ->
            grammar.getConnectCost(leftId.toShort(), rightId.toShort()).toInt()
        }
    }
}

class SudachiTermEntry(
        override val surfaceForm: String,
        override val leftId: Int,
        override val rightId: Int,
        override val cost: Int
) : TermEntry {

    companion object {
        fun fromLexicon(lexicon: Lexicon) : List<SudachiTermEntry> {
            val size = lexicon.size()
            val terms = Array(size) { wordId ->
                val cost = lexicon.getCost(wordId)
                val leftId = lexicon.getLeftId(wordId)
                val rightId = lexicon.getRightId(wordId)
                val wordInfo = lexicon.getWordInfo(wordId)

                SudachiTermEntry(wordInfo.surface, leftId.toInt(), rightId.toInt(), cost.toInt())
            }

            return terms.asList()
                    .filter { it.leftId >= 0 }
                    .filter { it.rightId >= 0 }
        }
    }
}