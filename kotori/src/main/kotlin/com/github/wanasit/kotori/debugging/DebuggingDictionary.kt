package com.github.wanasit.kotori.debugging

import com.github.wanasit.kotori.*
import com.github.wanasit.kotori.optimized.PlainTermEntry


class DebuggingFeatures<F>(
        val termID: TermID?,
        val termEntry: TermEntry<F>
)

fun <F> Dictionary<F>.toDebug() : DebuggingDictionary<F> {
    return DebuggingDictionary.create(this)
}

class DebuggingDictionary<F>(
        override val terms: DebuggingTermDictionary<F>,
        override val connection: ConnectionCost,
        override val unknownExtraction: DebuggingUnknownTermExtractionStrategy<F>?
) : Dictionary<DebuggingFeatures<F>>(terms, connection, unknownExtraction) {
    companion object {
        fun <F> create(originalDictionary: Dictionary<F>) : DebuggingDictionary<F> {
            return DebuggingDictionary(
                    DebuggingTermDictionary(originalDictionary.terms),
                    originalDictionary.connection,
                    DebuggingUnknownTermExtractionStrategy.create(originalDictionary.unknownExtraction)
            )
        }
    }
    
    class DebuggingUnknownTermExtractionStrategy<F> (
            private val original: UnknownTermExtractionStrategy<F>
    ) : UnknownTermExtractionStrategy<DebuggingFeatures<F>> {

        companion object {
            fun <F> create(original: UnknownTermExtractionStrategy<F>?): DebuggingUnknownTermExtractionStrategy<F>? {
                return if (original != null) {
                    DebuggingUnknownTermExtractionStrategy(original)
                } else {
                    null
                }
            }
        }

        override fun extractUnknownTerms(text: String, index: Int, forceExtraction: Boolean): Iterable<TermEntry<DebuggingFeatures<F>>> {
            return original.extractUnknownTerms(text, index, forceExtraction).map { createDebugEntry(it) }
        }

        private fun createDebugEntry(termEntry: TermEntry<F>): PlainTermEntry<DebuggingFeatures<F>> {
            val features = DebuggingFeatures(null, termEntry)
            return PlainTermEntry(termEntry, features)
        }
    }

    class DebuggingTermDictionary<F> (
            private val original: TermDictionary<F>
    ) : TermDictionary<DebuggingFeatures<F>> {
        override fun get(id: TermID): TermEntry<DebuggingFeatures<F>>? {
            val termEntry = original[id]
            if (termEntry != null) {
                return createDebugEntry(id, termEntry)
            }

            return termEntry
        }

        override fun iterator(): Iterator<Pair<TermID, TermEntry<DebuggingFeatures<F>>>> {
            return original.iterator().asSequence()
                    .map { it.first to createDebugEntry(it.first, it.second) }
                    .iterator()
        }

        private fun createDebugEntry(termId: TermID, termEntry: TermEntry<F>): PlainTermEntry<DebuggingFeatures<F>> {
            val features = DebuggingFeatures(termId, termEntry)
            return PlainTermEntry(termEntry, features)
        }
    }
}

