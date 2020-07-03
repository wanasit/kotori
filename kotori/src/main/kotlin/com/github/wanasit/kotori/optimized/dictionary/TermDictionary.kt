package com.github.wanasit.kotori.optimized.dictionary

import com.github.wanasit.kotori.TermDictionary
import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.TermEntryArray
import com.github.wanasit.kotori.utils.IOUtils
import java.io.InputStream
import java.io.OutputStream

class StandardTermDictionary(
        private val entries: Array<StandardTermEntry>
) : TermEntryArray<StandardTermEntry>(entries) {

    companion object {
        fun copyOf(termDictionary: TermDictionary<TermEntry>) : StandardTermDictionary {
            val terms = termDictionary
                    .map { StandardTermEntry(it.second) }
                    .toTypedArray()
            return StandardTermDictionary(terms)
        }

        fun readFromInputStream(inputStream: InputStream) : StandardTermDictionary {
            val termEntries = readStandardTermEntriesFromInputStream(inputStream)
            return StandardTermDictionary(termEntries)
        }
    }

    fun writeToOutputStream(outputStream: OutputStream) {
        writeStandardTermEntriesToOutput(outputStream, entries)
    }
}

class StandardTermEntry(
        override val surfaceForm: String,
        override val leftId: Int,
        override val rightId: Int,
        override val cost: Int
) : TermEntry {

    constructor(other: TermEntry) : this(
            surfaceForm=other.surfaceForm,
            leftId=other.leftId,
            rightId=other.rightId,
            cost=other.cost
    )
}

internal fun readStandardTermEntriesFromInputStream(inputStream: InputStream) : Array<StandardTermEntry> {
    val size = IOUtils.readInt(inputStream)
    val flattenTermEntry = IOUtils.readIntArray(inputStream, size * 3)
    val surfaceForms = IOUtils.readStringArray(inputStream, size)
    return Array(size) {
        StandardTermEntry(surfaceForms[it],
                flattenTermEntry[it*3], flattenTermEntry[it*3 + 1], flattenTermEntry[it*3 + 2])
    }
}

internal fun writeStandardTermEntriesToOutput(outputStream: OutputStream, termEntries: Array<StandardTermEntry>) {
    val size = termEntries.size
    val surfaceForms = termEntries.map { it.surfaceForm }.toTypedArray()
    val flattenTermEntry = termEntries.flatMap { listOf(it.leftId, it.rightId, it.cost) }.toIntArray()

    IOUtils.writeInt(outputStream, size)
    IOUtils.writeIntArray(outputStream, flattenTermEntry, includeSize = false)
    IOUtils.writeStringArray(outputStream, surfaceForms, includeSize = false)
}