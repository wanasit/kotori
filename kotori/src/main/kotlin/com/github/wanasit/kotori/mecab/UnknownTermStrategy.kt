package com.github.wanasit.kotori.mecab

import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.UnknownTermExtractionStrategy
import com.github.wanasit.kotori.utils.checkArgument
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Paths

data class ExtractedUnknownTermEntry(
        val unknownDictionaryEntry: MeCabTermEntry,
        val term: String) : TermEntry by unknownDictionaryEntry {

    override val surfaceForm: String = term
}

class MeCabUnknownTermExtractionStrategy(
        unknownDictionaryEntries: List<MeCabTermEntry>,
        charDefinitionLookup: MeCabCharDefinitionLookup
) : UnknownTermExtractionStrategy {

    companion object {

        fun readFromDirectory(
                dir: String, charset: Charset = MeCabDictionary.DEFAULT_CHARSET
        ) : MeCabUnknownTermExtractionStrategy = readFromInputStream(
                Paths.get(dir).resolve(MeCabDictionary.FILE_NAME_UNKNOWN_ENTRIES).toFile().inputStream(),
                Paths.get(dir).resolve(MeCabDictionary.FILE_NAME_CHARACTER_DEFINITION).toFile().inputStream(),
                charset)

        fun readFromInputStream(
                unknownDefinitionInputStream: InputStream,
                charDefinitionInputStream: InputStream,
                charset: Charset
        ) : MeCabUnknownTermExtractionStrategy {
            val unknownTermEntries = MeCabTermEntry.read(unknownDefinitionInputStream, charset)
            val charDefinitionLookup = MeCabCharDefinitionLookup.read(charDefinitionInputStream, charset)
            return MeCabUnknownTermExtractionStrategy(unknownTermEntries, charDefinitionLookup)
        }
    }

    private val indexedUnknownEntries: Map<Int, List<MeCabTermEntry>>
    private val charDefinitions = charDefinitionLookup;

    init {
        val indexedEntries = unknownDictionaryEntries.groupBy { it.surfaceForm }
        indexedUnknownEntries = charDefinitionLookup.categoryDefinitions()
                .mapValues { indexedEntries.get(it.value.categoryName) ?: emptyList() }
    }

    override fun extractUnknownTerms(text: String, index: Int, forceExtraction: Boolean): Iterable<TermEntry> {

        val results: MutableList<TermEntry> = mutableListOf()

        val charCategories = charDefinitions.lookupCharCategories(text[index])
        for (charCategory in charCategories) {
            val categoryDefinition = charDefinitions.lookupCategoryDefinition(charCategory)
            if (!forceExtraction && !categoryDefinition.invoke) {
                continue
            }

            val term = findConsecutiveCharsWithCategory(charCategory, text, index)
            indexedUnknownEntries[charCategory]?.forEach {
                results.add(ExtractedUnknownTermEntry(it, term))
            }
        }

        return results
    }

    private fun findConsecutiveCharsWithCategory(charCategory: Int, text: String, index: Int) : String {
        var i = index + 1
        while (i < text.length && charDefinitions.lookupCharCategories(text[i]).contains(charCategory)) {
            i += 1
        }

        return text.substring(index, i)
    }
}


data class CharCategoryDefinition(
        val categoryName: String,
        val invoke: Boolean,
        val group: Boolean,
        val length: Int
)

class MeCabCharDefinitionLookup constructor(
        categoryDefinitions: List<CharCategoryDefinition>,
        mappingEntries: List<Triple<String, Int, Int>>
) {

    private val definitions: List<CharCategoryDefinition> = categoryDefinitions;
    private val table: Array<IntArray?>;

    fun lookupCharCategories(charCode: Char): IntArray {
        return table[charCode.toInt()] ?: intArrayOf(0)
    }

    fun lookupCategoryDefinition(categoryId: Int) : CharCategoryDefinition {
        return definitions[categoryId]
    }

    fun categoryDefinitions() : Map<Int, CharCategoryDefinition> {
        return definitions
                .mapIndexed { id, def -> id to def}
                .toMap()
    }

    init {
        val tmpTable = Array(0xffff) { mutableListOf<Int>() }
        val categoryNameLookup = categoryDefinitions.withIndex()
                .associate { it.value.categoryName to it.index }
        mappingEntries.forEach {

            val definitionIndex = categoryNameLookup.get(it.first)
                    ?: throw IllegalArgumentException("Unknown category name '${it.first}'");

            for (i in it.second..it.third) {
                tmpTable[i].add(definitionIndex)
            }
        }
        table = tmpTable.map { if (it.isEmpty()) null else it.toIntArray() }.toTypedArray()
    }

    companion object {

        fun read(inputStream: InputStream, charset: Charset) : MeCabCharDefinitionLookup {
            return read(inputStream
                    .reader(charset = charset)
                    .readLines())
        }

        fun read(lines: List<String>) : MeCabCharDefinitionLookup {

            /** char.def example
            # This is comment
            ...
            DEFAULT         0 1 0  # DEFAULT is a mandatory category!
            SPACE           0 1 0
            ...
            0xFF10..0xFF19 NUMERIC
            ...

            0x3007 SYMBOL KANJINUMERIC
             **/
            val commentRegEx = "\\s*#.*".toRegex();

            val categoryDefinitions: MutableList<CharCategoryDefinition> = mutableListOf()
            val mappingEntries: MutableList<Triple<String, Int, Int>> = mutableListOf()

            lines
                    .map { commentRegEx.replace(it, "").trim() }
                    .filter { it.isNotEmpty() }
                    .forEach { line: String ->

                        if (line.startsWith("0x")) {
                            mappingEntries.addAll(parseMapping(line))

                        } else {
                            val definition = parseCategory(line)
                            if (definition.categoryName == "DEFAULT") {
                                categoryDefinitions.add(0, definition)
                            } else {
                                categoryDefinitions.add(definition);
                            }
                        }

                        line.chars()
                    }

            return MeCabCharDefinitionLookup(categoryDefinitions, mappingEntries);
        }

        private fun parseCategory(input: String): CharCategoryDefinition {

            val whiteSpaceRegEx = "\\s+".toRegex();
            val values = whiteSpaceRegEx.split(input)

            val classname = values[0]
            val invoke = values[1].toInt() == 1
            val group = values[2].toInt() == 1
            val length = values[3].toInt()

            return CharCategoryDefinition(classname, invoke, group, length);
        }

        private fun parseMapping(input: String) : Iterable<Triple<String, Int, Int>> {

            val whiteSpaceRegEx = "\\s+".toRegex();
            val rangeSymbolRegex = "\\.\\.".toRegex();

            val values = whiteSpaceRegEx.split(input)
            checkArgument(values.size >= 2)
            if (values.size == 1) {
                print("acg")
            }
            val codepointParts = rangeSymbolRegex.split(values[0]);
            val categories: List<String> = values.drop(1);

            val lowerCodepoint: Int;
            val upperCodepoint: Int;
            if (codepointParts.size == 2) {
                lowerCodepoint = Integer.decode(codepointParts[0])
                upperCodepoint = Integer.decode(codepointParts[1])
            } else {
                lowerCodepoint = Integer.decode(codepointParts[0])
                upperCodepoint = Integer.decode(codepointParts[0])
            }

            return categories.map { Triple(it, lowerCodepoint, upperCodepoint + 1) }
        }
    }
}
