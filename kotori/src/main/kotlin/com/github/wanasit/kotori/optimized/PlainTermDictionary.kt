package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.TermDictionary
import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.TermID

open class PlainTermDictionary<TermFeatures>(
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
        ) : PlainTermDictionary<DstFeatures> {
            return copyOf(other.map { it.second }, transformEntry)
        }

        fun <SrcFeatures, DstFeatures> copyOf(
                other: List<TermEntry<SrcFeatures>>,
                transformEntry: (TermEntry<SrcFeatures>) -> TermEntry<DstFeatures>
        ) : PlainTermDictionary<DstFeatures> {
            val entries = other.map(transformEntry).toTypedArray()
            return PlainTermDictionary(entries)
        }
    }
}