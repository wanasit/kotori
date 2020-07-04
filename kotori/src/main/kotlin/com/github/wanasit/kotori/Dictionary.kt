package com.github.wanasit.kotori

import com.github.wanasit.kotori.optimized.DefaultDictionary
import com.github.wanasit.kotori.optimized.DefaultTermFeatures


open class Dictionary <out TermFeatures> (
        open val terms: TermDictionary<TermFeatures>,
        open val connection: ConnectionCost,
        open val unknownExtraction: UnknownTermExtractionStrategy<TermFeatures>? = null
) {
    companion object {
        @JvmStatic
        fun readDefaultFromResource(): Dictionary<DefaultTermFeatures> {
            return DefaultDictionary.readFromResource()
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
interface TermEntry<out Features> {
    val surfaceForm: String
    val leftId: Int
    val rightId: Int
    val cost: Int

    val features: Features
}

typealias TermID = Int
interface TermDictionary<out TermFeatures> : Iterable<Pair<TermID, TermEntry<TermFeatures>>>{
    operator fun get(id: TermID): TermEntry<TermFeatures>?

    fun size() : Int = this.asSequence().count()
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
interface UnknownTermExtractionStrategy<out TermFeatures> {

    /**
     * Extract unknown terms from `text` at `index`
     *
     * @param forceExtraction at least one term is expected to be extracted when this flag is enforced
     */
    fun extractUnknownTerms(text: String, index: Int, forceExtraction: Boolean): Iterable<TermEntry<TermFeatures>>
}

