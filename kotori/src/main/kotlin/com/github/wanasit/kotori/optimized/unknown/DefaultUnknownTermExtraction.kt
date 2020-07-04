package com.github.wanasit.kotori.optimized.unknown

import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.UnknownTermExtractionStrategy
import com.github.wanasit.kotori.optimized.DefaultTermFeatures
import com.github.wanasit.kotori.utils.IOUtils
import java.io.InputStream
import java.io.OutputStream

object DefaultUnknownTermExtraction {

    fun readFromInputStream(
            inputStream: InputStream): UnknownTermExtractionByCharacterCategory<DefaultTermFeatures>? {

        val charCategorySize = IOUtils.readInt(inputStream)
        if (charCategorySize == 0) {
            return null
        }

        val charcodeSize = 0xffff + 1
        val categoryDefinitionFlattenArray = IOUtils.readShortArray(inputStream)
        val arraySizes = IOUtils.readIntArray(inputStream)
        val flattenCharToCategories = IOUtils.readIntArray(inputStream)
        val flattenCategoryToTermEntries = DefaultTermFeatures.readTermEntriesFromInputStream(inputStream)

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

    fun writeToOutputStream(outputStream: OutputStream, value: UnknownTermExtractionByCharacterCategory<DefaultTermFeatures>?) {
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
        DefaultTermFeatures.writeTermEntriesToOutput(outputStream, flattenCategoryToTermEntries.toTypedArray())
    }
}


