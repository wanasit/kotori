package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.utils.IOUtils
import java.io.InputStream
import java.io.OutputStream

class DefaultTermFeatures(

) {





    companion object {
        fun readTermEntriesFromInputStream(inputStream: InputStream) : Array<TermEntry<DefaultTermFeatures>> {
            val size = IOUtils.readInt(inputStream)
            val flattenTermEntry = IOUtils.readIntArray(inputStream, size * 3)
            val surfaceForms = IOUtils.readStringArray(inputStream, size)
            return Array(size) {
                PlainTermEntry(surfaceForms[it],
                        flattenTermEntry[it*3], flattenTermEntry[it*3 + 1], flattenTermEntry[it*3 + 2], DefaultTermFeatures())
            }
        }

        fun writeTermEntriesToOutput(outputStream: OutputStream, termEntries: Array<TermEntry<DefaultTermFeatures>>) {
            val size = termEntries.size
            val surfaceForms = termEntries.map { it.surfaceForm }.toTypedArray()
            val flattenTermEntry = termEntries.flatMap { listOf(it.leftId, it.rightId, it.cost) }.toIntArray()

            IOUtils.writeInt(outputStream, size)
            IOUtils.writeIntArray(outputStream, flattenTermEntry, includeSize = false)
            IOUtils.writeStringArray(outputStream, surfaceForms, includeSize = false)
        }
    }
}