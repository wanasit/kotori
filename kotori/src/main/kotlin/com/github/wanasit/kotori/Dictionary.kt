package com.github.wanasit.kotori

import com.github.wanasit.kotori.mecab.MeCabDictionary


class Dictionary <out T: TermEntry> (
        val terms: TermDictionary<T>,
        val connection: ConnectionCost,
        val unknownExtraction: UnknownTermExtractionStrategy? = null
) {
    companion object {

        @JvmStatic
        fun readDefaultFromResource(): Dictionary<TermEntry> {
            return MeCabDictionary.readFromResource()
        }
    }
}

/**
 * TermDictionary (単語辞書)
 *
 * e.g.
 * - 木, 1285 (Noun), 1285 (Noun), 7283
 * - 切る, 772 (Verb-ru), 772 (Verb-ru), 7439
 * - きる, 772 (Verb-ru), 772 (Verb-ru), 12499
 * - ...
 */
typealias TermID = Int
interface TermDictionary<out T: TermEntry> : Iterable<Pair<TermID, T>>{
    operator fun get(id: TermID): T?
}

interface TermEntry {
    val surfaceForm: String
    val leftId: Int
    val rightId: Int
    val cost: Int
}

/**
 * ConnectionCost (連接コース) or Connectivity ()
 *
 * e.g.
 * - 1285 (Noun) to 1285 (Noun) => 62
 * - 772 (Verb-ru) to 1285 (Noun) => 335
 * - 772 (Verb-ru) to 772 (Verb-ru) => -3713
 */
interface ConnectionCost {
    fun lookup(fromRightId: Int, toLeftId: Int): Int
}

/**
 * Unknown or Out-of-Vocabulary terms handling strategy
 */
interface UnknownTermExtractionStrategy {

    /**
     * Extract unknown terms from `text` at `index`
     *
     * @param forceExtraction at least one term is expected to be extracted when this flag is enforced
     */
    fun extractUnknownTerms(text: String, index: Int, forceExtraction: Boolean): Iterable<TermEntry>
}

