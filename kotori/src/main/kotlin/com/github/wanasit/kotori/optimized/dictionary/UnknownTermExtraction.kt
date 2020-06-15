package com.github.wanasit.kotori.optimized.dictionary

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
class UnknownTermExtractionByCharacterCategory<T: TermEntry>(
        internal val charToCategories: Array<IntArray>,
        internal val categoryToDefinition: Array<CharCategoryDefinition>,
        internal val categoryToTermEntries: Array<List<T>>
) : UnknownTermExtractionStrategy {

    companion object {

        fun <T: TermEntry> fromUnoptimizedMapping(
                charToCategories: Map<Char, List<CharCategory>?>,
                categoryToDefinition: Map<CharCategory, CharCategoryDefinition>,
                categoryToTermEntries: Map<CharCategory, List<T>?>
        ) : UnknownTermExtractionByCharacterCategory<T> {

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

        fun <T: TermEntry> copyOf(
                other: UnknownTermExtractionByCharacterCategory<*>,
                transformEntry: (TermEntry) -> T
        ) : UnknownTermExtractionByCharacterCategory<T> {
            val termEntryMapping = other.categoryToTermEntries.map { it.map(transformEntry) }.toTypedArray()
            return UnknownTermExtractionByCharacterCategory(
                    other.charToCategories, other.categoryToDefinition, termEntryMapping)
        }
    }

    data class ExtractedUnknownTermEntry<T: TermEntry>(
            val unknownDictionaryEntry: T,
            val term: String) : TermEntry by unknownDictionaryEntry {
        override val surfaceForm: String = term
    }

    override fun extractUnknownTerms(text: String, index: Int, forceExtraction: Boolean): Iterable<ExtractedUnknownTermEntry<T>> {
        val results: MutableList<ExtractedUnknownTermEntry<T>> = mutableListOf()
        val charCategories = charToCategories[text[index].toInt()]
        for (charCategory in charCategories) {
            val categoryDefinition = categoryToDefinition[charCategory]
            if (!forceExtraction && !categoryDefinition.invoke) {
                continue
            }

            val term = findConsecutiveCharsWithCategory(charCategory, text, index)
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


