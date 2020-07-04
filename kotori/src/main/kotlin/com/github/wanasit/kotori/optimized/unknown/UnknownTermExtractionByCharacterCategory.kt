package com.github.wanasit.kotori.optimized.unknown

import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.UnknownTermExtractionStrategy

typealias CharCategory = Int

data class CharCategoryDefinition(
        val invoke: Boolean,
        val group: Boolean,
        val length: Short
)

/**
 * An unknown term extraction used by MeCab
 * This implementation is in an optimized form (see. fromUnoptimizedMapping() for the logical input)
 *
 * In this extraction strategy:
 * - Each Java character (UTF-16) has categories
 * - Each category has a definition and extractable term entries
 * - The extraction considers the categories and their definitions of character at the index
 */
class UnknownTermExtractionByCharacterCategory<Features>(
        internal val charToCategories: Array<IntArray>,
        internal val categoryToDefinition: Array<CharCategoryDefinition>,
        internal val categoryToTermEntries: Array<List<TermEntry<Features>>>
) : UnknownTermExtractionStrategy<Features> {

    companion object {

        fun <Features> fromUnoptimizedMapping(
                charToCategories: Map<Char, List<CharCategory>?>,
                categoryToDefinition: Map<CharCategory, CharCategoryDefinition>,
                categoryToTermEntries: Map<CharCategory, List<TermEntry<Features>>?>
        ) : UnknownTermExtractionByCharacterCategory<Features> {

            val charSize = 0xffff + 1;

            val compactedCharToCategories = Array(charSize) {
                charToCategories[it.toChar()]?.toIntArray() ?: intArrayOf() }

            val compactedDefinitionMapping = Array(categoryToDefinition.size) {
                categoryToDefinition[it] ?: error("Definition keys should be [0 to SIZE-1] got ${categoryToDefinition.keys}")
            }

            val compactedTermEntryMapping = Array(categoryToDefinition.size) {
                categoryToTermEntries[it]?.toList() ?: emptyList()
            }

            return UnknownTermExtractionByCharacterCategory(compactedCharToCategories, compactedDefinitionMapping, compactedTermEntryMapping)
        }

        fun <SrcFeatures, DstFeatures> copyOf(
                other: UnknownTermExtractionByCharacterCategory<SrcFeatures>,
                transformEntry: (TermEntry<SrcFeatures>) -> TermEntry<DstFeatures>
        ) : UnknownTermExtractionByCharacterCategory<DstFeatures> {
            val termEntryMapping = other.categoryToTermEntries.map { it.map(transformEntry) }.toTypedArray()
            return UnknownTermExtractionByCharacterCategory(
                    other.charToCategories, other.categoryToDefinition, termEntryMapping)
        }
    }

    data class ExtractedUnknownTermEntry<Features>(
            val unknownDictionaryEntry: TermEntry<Features>,
            val term: String) : TermEntry<Features> by unknownDictionaryEntry {
        override val surfaceForm: String = term
    }

    override fun extractUnknownTerms(text: String, index: Int, forceExtraction: Boolean): Iterable<ExtractedUnknownTermEntry<Features>> {
        val results: MutableList<ExtractedUnknownTermEntry<Features>> = mutableListOf()
        val charCategories = charToCategories[text[index].toInt()]
        for (charCategory in charCategories) {
            val categoryDefinition = categoryToDefinition[charCategory]
            if (!forceExtraction && !categoryDefinition.invoke) {
                continue
            }

            val term = if (categoryDefinition.group) {
                findConsecutiveCharsWithCategory(charCategory, text, index)
            } else {
                text.substring(index, index + 1)
            }

            categoryToTermEntries[charCategory].forEach {
                results.add(ExtractedUnknownTermEntry(it, term))
            }
        }

        return results
    }

    private fun findConsecutiveCharsWithCategory(charCategory: CharCategory, text: String, index: Int) : String {
        var i = index + 1
        while (i < text.length && charToCategories[text[i].toInt()].contains(charCategory)) {
            i += 1
        }

        return text.substring(index, i)
    }
}


