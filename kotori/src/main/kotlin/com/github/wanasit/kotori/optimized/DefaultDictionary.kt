package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.TermDictionary
import com.github.wanasit.kotori.TermID
import com.github.wanasit.kotori.optimized.unknown.DefaultUnknownTermExtraction
import com.github.wanasit.kotori.optimized.unknown.UnknownTermExtractionByCharacterCategory
import com.github.wanasit.kotori.utils.ResourceUtil
import com.github.wanasit.kotori.utils.termEntries
import java.io.InputStream
import java.io.OutputStream



class DefaultDictionary(
        override val terms: TermDictionary<DefaultTermFeatures>,
        override val connection: PlainConnectionCostTable,
        override val unknownExtraction: UnknownTermExtractionByCharacterCategory<DefaultTermFeatures>?
): Dictionary<DefaultTermFeatures>(terms, connection, unknownExtraction) {

    companion object {
        const val DEFAULT_RESOURCE_FILE: String = "default_dictionary.bin"

        fun readFromResource(
                filename: String = DEFAULT_RESOURCE_FILE,
                namespace: String = ""
        ) : DefaultDictionary {
            val inputStream = ResourceUtil.readResourceAsStream(namespace, filename)
            return readFromInputStream(inputStream)
        }

        fun readFromInputStream(inputStream: InputStream) : DefaultDictionary {
            val unknownExtraction = DefaultUnknownTermExtraction.readFromInputStream(inputStream)
            val connection = PlainConnectionCostTable.readFromInputStream(inputStream)
            val termEntries = DefaultTermFeatures.readTermEntriesFromInputStream(inputStream)

            return DefaultDictionary(PlainTermDictionary(termEntries), connection, unknownExtraction)
        }

        fun writeToOutputStream(outputStream: OutputStream, value: DefaultDictionary) {
            val termEntries = value.termEntries.toTypedArray()
            DefaultUnknownTermExtraction.writeToOutputStream(outputStream, value.unknownExtraction)
            PlainConnectionCostTable.writeToOutputStream(outputStream, value.connection)
            DefaultTermFeatures.writeTermEntriesToOutput(outputStream, termEntries)
        }
    }
}



