package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.mecab.MeCabLikeTermFeatures
import com.github.wanasit.kotori.mecab.MeCabTermFeatures
import com.github.wanasit.kotori.utils.IOUtils
import java.io.InputStream
import java.io.OutputStream
import java.lang.IllegalArgumentException

/**
 * A term entry for default dictionary
 * To make default dictionary compact, we make assumption that
 *   the left and right context id of each term entry are the same
 */
data class DefaultTermEntry(
        override val surfaceForm: String,
        val contextId: Int,
        override val cost: Int,
        override val features: DefaultTermFeatures,
        override val leftId: Int = contextId,
        override val rightId: Int = contextId
) : TermEntry<DefaultTermFeatures> {

    companion object {
        fun copy(other: TermEntry<DefaultTermFeatures>) : DefaultTermEntry {
            if (other.leftId != other.rightId) {
                throw IllegalArgumentException(
                        "A default term entry must have the same left and right context ID")
            }

            return DefaultTermEntry(
                    surfaceForm = other.surfaceForm,
                    contextId = other.leftId,
                    cost = other.cost,
                    features = other.features)
        }

        fun readFromInputStream(inputStream: InputStream) : Array<DefaultTermEntry> {
            val size = IOUtils.readInt(inputStream)
            val sizePerEntry = 3
            val flattenTermEntry = IOUtils.readIntArray(inputStream, size * sizePerEntry)
            val surfaceForms = IOUtils.readStringArray(inputStream, size)
            return Array(size) {
                DefaultTermEntry(
                        surfaceForm = surfaceForms[it],
                        contextId = flattenTermEntry[it*sizePerEntry],
                        cost = flattenTermEntry[it*sizePerEntry + 1],
                        features = DefaultTermFeatures(
                                partOfSpeech = DefaultTermFeatures.PartOfSpeech.values()[flattenTermEntry[it*sizePerEntry + 2]]
                        ))
            }
        }

        fun writeToOutputAsDefaultTermEntries(outputStream: OutputStream, termEntries: Array<TermEntry<DefaultTermFeatures>>) {
            writeToOutput(outputStream, termEntries.map { copy(it) }.toTypedArray() )
        }

        fun writeToOutput(outputStream: OutputStream, termEntries: Array<DefaultTermEntry>) {
            val size = termEntries.size
            val surfaceForms = termEntries.map { it.surfaceForm }.toTypedArray()
            val flattenTermEntry = termEntries.flatMap { listOf(
                    it.contextId,
                    it.cost,
                    it.features.partOfSpeech.ordinal
            )}.toIntArray()

            IOUtils.writeInt(outputStream, size)
            IOUtils.writeIntArray(outputStream, flattenTermEntry, includeSize = false)
            IOUtils.writeStringArray(outputStream, surfaceForms, includeSize = false)
        }
    }
}
