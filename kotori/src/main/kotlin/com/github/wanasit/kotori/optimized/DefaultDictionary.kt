package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.TermDictionary
import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.optimized.unknown.DefaultUnknownTermExtraction
import com.github.wanasit.kotori.optimized.unknown.UnknownTermExtractionByCharacterCategory
import com.github.wanasit.kotori.utils.ResourceUtil
import com.github.wanasit.kotori.utils.termEntries
import java.io.InputStream
import java.io.OutputStream

class DefaultTermEntry<Features>(
        override val surfaceForm: String,
        override val leftId: Int,
        override val rightId: Int,
        override val cost: Int,
        override val features: Features
) : TermEntry<Features> {
    constructor(other: TermEntry<*>, features: Features) :
            this(other.surfaceForm, other.leftId, other.rightId, other.cost, features)
}

class DefaultDictionary(
        override val terms: TermDictionary<DefaultTermFeatures>,
        override val connection: DefaultConnectionCost,
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
            val connection = DefaultConnectionCost.readFromInputStream(inputStream)
            val termEntries = DefaultTermFeatures.readTermEntriesFromInputStream(inputStream)

            return DefaultDictionary(DefaultTermDictionary(termEntries), connection, unknownExtraction)
        }

        fun writeToOutputStream(outputStream: OutputStream, value: DefaultDictionary) {
            val termEntries = value.termEntries.toTypedArray()
            DefaultUnknownTermExtraction.writeToOutputStream(outputStream, value.unknownExtraction)
            DefaultConnectionCost.writeToOutputStream(outputStream, value.connection)
            DefaultTermFeatures.writeTermEntriesToOutput(outputStream, termEntries)
        }
    }
}



