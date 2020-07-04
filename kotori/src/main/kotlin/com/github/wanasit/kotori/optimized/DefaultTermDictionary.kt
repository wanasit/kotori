package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.TermDictionary
import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.TermID
import com.github.wanasit.kotori.optimized.unknown.UnknownTermExtractionByCharacterCategory

open class DefaultTermDictionary<TermFeatures>(
        private val entries: Array<out TermEntry<TermFeatures>>
) : TermDictionary<TermFeatures> {






    override fun get(id: TermID): TermEntry<TermFeatures>? = entries[id]
    override fun size(): Int = entries.size
    override fun iterator(): Iterator<Pair<TermID, TermEntry<TermFeatures>>> =
            entries.indices.map { it to entries[it] }.iterator()

    companion object {
        fun <SrcFeatures, DstFeatures> copyOf(
                other: TermDictionary<SrcFeatures>,
                transformEntry: (TermEntry<SrcFeatures>) -> TermEntry<DstFeatures>
        ) : DefaultTermDictionary<DstFeatures> {
            val entries = other.map { transformEntry(it.second) }.toTypedArray()
            return DefaultTermDictionary(entries)
        }
    }
}