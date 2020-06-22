package com.github.wanasit.kotori.optimized.dictionary

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.utils.IOUtils
import com.github.wanasit.kotori.utils.ResourceUtil
import java.io.InputStream
import java.io.OutputStream

class OptimizedDictionary(
        override val terms: StandardTermDictionary,
        override val connection: ConnectionCostArray,
        override val unknownExtraction: UnknownTermExtractionByCharacterCategory<StandardTermEntry>?
) : Dictionary<StandardTermEntry>(terms, connection, unknownExtraction) {

    companion object {
        const val DEFAULT_RESOURCE_FILE: String = "default_dictionary.bin"

        fun copyOf(dictionary: Dictionary<*>): OptimizedDictionary {
            val termDictionary = StandardTermDictionary.copyOf(dictionary.terms)
            val connectionCost = ConnectionCostArray.copyOf(termDictionary.map { it.second }, dictionary.connection)
            val unknownExtraction = if (dictionary.unknownExtraction is UnknownTermExtractionByCharacterCategory<*>) {
                UnknownTermExtractionByCharacterCategory.copyOf(dictionary.unknownExtraction as UnknownTermExtractionByCharacterCategory<*>) { StandardTermEntry(it) }
            } else null

            return OptimizedDictionary(termDictionary, connectionCost, unknownExtraction)
        }

        fun readFromResource(
                filename: String = DEFAULT_RESOURCE_FILE,
                namespace: String = ""
        ) : OptimizedDictionary {
            val inputStream = ResourceUtil.readResourceAsStream(namespace, filename)
            return readFromInputStream(inputStream)
        }

        fun readFromInputStream(inputStream: InputStream) : OptimizedDictionary {

            val unknownExtraction = tryReadingUnknownTermFromInputStream(inputStream)
            val connection = ConnectionCostArray.readFromInputStream(inputStream)
            val terms = StandardTermDictionary.readFromInputStream(inputStream)

            return OptimizedDictionary(terms, connection, unknownExtraction)
        }

        private fun tryReadingUnknownTermFromInputStream(
                inputStream: InputStream): UnknownTermExtractionByCharacterCategory<StandardTermEntry>? {

            val charCategorySize = IOUtils.readInt(inputStream)
            if (charCategorySize == 0) {
                return null
            }

            val charcodeSize = 0xffff + 1
            val categoryDefinitionFlattenArray = IOUtils.readShortArray(inputStream)
            val arraySizes = IOUtils.readIntArray(inputStream)
            val flattenCharToCategories = IOUtils.readIntArray(inputStream)
            val flattenCategoryToTermEntries = readStandardTermEntriesFromInputStream(inputStream)

            var index = 0
            val charToCategories: Array<IntArray> = Array(charcodeSize) {
                val categories = flattenCharToCategories.copyOfRange(index, index + arraySizes[it])
                index += arraySizes[it]
                categories
            }

            index = 0
            val categoryToTermEntries = Array(charCategorySize) {
                val entries = flattenCategoryToTermEntries.copyOfRange(
                        index, index + arraySizes[charcodeSize + it]).toList()
                index += arraySizes[charcodeSize + it]
                entries
            }

            val categoryToDefinition = Array(charCategorySize) {
                CharCategoryDefinition(
                        categoryDefinitionFlattenArray[it * 3] > 0,
                        categoryDefinitionFlattenArray[it * 3 + 1] > 0,
                        categoryDefinitionFlattenArray[it * 3 + 2]
                )
            }

            return UnknownTermExtractionByCharacterCategory(charToCategories, categoryToDefinition, categoryToTermEntries)
        }

        private fun tryWritingUnknownTermToOutputStream(
                outputStream: OutputStream, value: UnknownTermExtractionByCharacterCategory<StandardTermEntry>?) {

            if (value == null) {
                IOUtils.writeInt(outputStream, 0)
                return
            }

            val charCategorySize = value.categoryToDefinition.size
            if (charCategorySize == 0) {
                IOUtils.writeInt(outputStream, 0)
                return
            }


            val categoryDefinitionFlattenArray = value.categoryToDefinition.flatMap { listOf(
                    (if (it.invoke) 1 else 0).toShort(),
                    (if (it.group) 1 else 0).toShort(),
                    it.length
            )}.toShortArray()


            val charcodeSize = 0xffff + 1
            val arraySizes = IntArray( charcodeSize + charCategorySize)
            value.charToCategories.indices.forEach { arraySizes[it] = value.charToCategories[it].size }
            value.categoryToTermEntries.indices.forEach { arraySizes[it + charcodeSize] = value.categoryToTermEntries[it].size }

            val flattenCharToCategories = value.charToCategories.flatMap { it.asList() }.toIntArray()
            val flattenCategoryToTermEntries = value.categoryToTermEntries.flatMap { it }

            IOUtils.writeInt(outputStream, charCategorySize)
            IOUtils.writeShortArray(outputStream, categoryDefinitionFlattenArray)
            IOUtils.writeIntArray(outputStream, arraySizes)
            IOUtils.writeIntArray(outputStream, flattenCharToCategories)
            writeStandardTermEntriesToOutput(outputStream, flattenCategoryToTermEntries.toTypedArray())
        }
    }

    fun writeToOutputStream(outputStream: OutputStream) {
        tryWritingUnknownTermToOutputStream(outputStream, unknownExtraction)
        connection.writeToOutputStream(outputStream)
        terms.writeToOutputStream(outputStream)
    }
}




