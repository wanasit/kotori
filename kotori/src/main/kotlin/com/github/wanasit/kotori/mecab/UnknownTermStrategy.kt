package com.github.wanasit.kotori.mecab

import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.optimized.dictionary.CharCategory
import com.github.wanasit.kotori.optimized.dictionary.CharCategoryDefinition
import com.github.wanasit.kotori.optimized.dictionary.UnknownTermExtractionByCharacterCategory
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Paths

data class ExtractedUnknownTermEntry(
        val unknownDictionaryEntry: MeCabTermEntry,
        val term: String) : TermEntry by unknownDictionaryEntry {

    override val surfaceForm: String = term
}

object MeCabUnknownTermExtractionStrategy {

    fun readFromDirectory(
            dir: String, charset: Charset = MeCabDictionary.DEFAULT_CHARSET
    ) : UnknownTermExtractionByCharacterCategory<MeCabTermEntry> = readFromFileInputStreams(
            Paths.get(dir).resolve(MeCabDictionary.FILE_NAME_UNKNOWN_ENTRIES).toFile().inputStream(),
            Paths.get(dir).resolve(MeCabDictionary.FILE_NAME_CHARACTER_DEFINITION).toFile().inputStream(),
            charset)

    fun readFromFileInputStreams(
            unknownDefinitionInputStream: InputStream,
            charDefinitionInputStream: InputStream,
            charset: Charset
    ) : UnknownTermExtractionByCharacterCategory<MeCabTermEntry> {
        val unknownTermEntries = MeCabTermEntry.readEntriesFromFileInputStream(unknownDefinitionInputStream, charset)
        val charDefinitionLookup = MeCabCharDefinition
                .readFromCharDefinitionFileInputStream(charDefinitionInputStream, charset)
        return create(unknownTermEntries, charDefinitionLookup)
    }

    fun create(
            unknownTermEntries: List<MeCabTermEntry>,
            charDefinition: MeCabCharDefinition
    ): UnknownTermExtractionByCharacterCategory<MeCabTermEntry> {

        val categoryNameLookup = charDefinition.createCategoryNameLookup()
        val charToCategories = charDefinition.createCharToCategoryMapping(categoryNameLookup)
        val definitionMapping = charDefinition.createCategoryToDefinition()

        val termEntryMapping = unknownTermEntries.groupBy { it.surfaceForm }
                .mapKeys { categoryNameLookup[it.key]
                        ?: throw IllegalArgumentException("Unknown category name '${it.key}'") }

        return UnknownTermExtractionByCharacterCategory.fromUnoptimizedMapping(
                charToCategories, definitionMapping, termEntryMapping)
    }
}

/**
 * This class represent the character definition as defined in char.def file
 */
class MeCabCharDefinition constructor(
        val categoryDefinitions: List<MecabCharCategoryDefinition>,
        val categoryCharCodeRanges: List<Triple<String, Int, Int>>
) {
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
    data class MecabCharCategoryDefinition(
            val categoryName: String,
            val invoke: Boolean,
            val group: Boolean,
            val length: Short
    ) {
        fun toCharCategoryDefinition() : CharCategoryDefinition {
            return CharCategoryDefinition(invoke, group, length)
        }
    }

    companion object {

        fun readFromCharDefinitionFileInputStream(inputStream: InputStream, charset: Charset) : MeCabCharDefinition {
            return readFromLines(inputStream
                    .reader(charset = charset)
                    .readLines())
        }

        fun readFromLines(lines: List<String>) : MeCabCharDefinition {
            val commentRegEx = "\\s*#.*".toRegex();

            val categoryDefinitions: MutableList<MecabCharCategoryDefinition> = mutableListOf()
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

            return MeCabCharDefinition(categoryDefinitions, mappingEntries);
        }

        private fun parseCategory(input: String): MecabCharCategoryDefinition {

            val whiteSpaceRegEx = "\\s+".toRegex();
            val values = whiteSpaceRegEx.split(input)

            val classname = values[0]
            val invoke = values[1].toInt() == 1
            val group = values[2].toInt() == 1
            val length = values[3].toShort()

            return MecabCharCategoryDefinition(classname, invoke, group, length);
        }

        private fun parseMapping(input: String) : Iterable<Triple<String, Int, Int>> {

            val whiteSpaceRegEx = "\\s+".toRegex();
            val rangeSymbolRegex = "\\.\\.".toRegex();

            val values = whiteSpaceRegEx.split(input)
            check(values.size >= 2)
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

    fun createCategoryNameLookup() : Map<String, CharCategory> {
        return categoryDefinitions.withIndex().associate { it.value.categoryName to it.index }
    }

    fun createCharToCategoryMapping(
            categoryNameLookup: Map<String, CharCategory> = createCategoryNameLookup()
    ): Map<Char, List<CharCategory>> {

        val tmpArray: Array<MutableList<Int>?> = arrayOfNulls(0xffff + 1)
        categoryCharCodeRanges.forEach {
            val charCategory = categoryNameLookup[it.first]
                    ?: throw IllegalArgumentException("Unknown category name '${it.first}'");

            for (i in it.second..it.third) {

                if (tmpArray[i] == null) {
                    tmpArray[i] = mutableListOf(charCategory)
                } else {
                    tmpArray[i]?.add(charCategory)
                }
            }
        }

        return tmpArray.mapIndexed {
            charCode, categories-> charCode.toChar() to (categories ?: listOf(0))
        }.toMap()
    }

    fun createCategoryToDefinition(): Map<CharCategory, CharCategoryDefinition> {
        return categoryDefinitions
                .mapIndexed { i : CharCategory, definition ->  i to definition.toCharCategoryDefinition() }
                .toMap()
    }
}
