package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.mecab.MeCabLikeTermFeatures
import com.github.wanasit.kotori.mecab.MeCabTermFeatures
import com.github.wanasit.kotori.utils.IOUtils
import java.io.InputStream
import java.io.OutputStream

data class DefaultTermFeatures(
    val partOfSpeech: PartOfSpeech = PartOfSpeech.UNKNOWN
) {

    enum class PartOfSpeech(vararg val labels: String) {
        ADJECTIVE("形容詞"),
        ADNOMINAL("連体詞"),
        ADVERB("副詞"),
        AUXILIARY("助動詞"),
        CONJUNCTION("接続詞"),
        INTERJECTION("感動詞", "フィラー"),
        NOUN("名詞"),
        PARTICLE("助詞"),
        PREFIX("接頭詞"),
        SUFFIX("(名詞)接尾"),
        SYMBOL("記号"),
        VERB("動詞"),

        OTHER(),
        UNKNOWN()
    }

    companion object {
        fun readTermEntriesFromInputStream(inputStream: InputStream) : Array<TermEntry<DefaultTermFeatures>> {
            val size = IOUtils.readInt(inputStream)
            val sizePerEntry = 4
            val flattenTermEntry = IOUtils.readIntArray(inputStream, size * sizePerEntry)
            val surfaceForms = IOUtils.readStringArray(inputStream, size)
            return Array(size) {
                val leftId = flattenTermEntry[it*sizePerEntry]
                val rightId = flattenTermEntry[it*sizePerEntry + 1]
                val cost = flattenTermEntry[it*sizePerEntry + 2]

                PlainTermEntry(surfaceForms[it],
                        leftId, rightId, cost,
                        DefaultTermFeatures(
                            partOfSpeech = PartOfSpeech.values()[flattenTermEntry[it*sizePerEntry + 3]]
                ))
            }
        }

        fun writeTermEntriesToOutput(outputStream: OutputStream, termEntries: Array<TermEntry<DefaultTermFeatures>>) {
            val size = termEntries.size
            val surfaceForms = termEntries.map { it.surfaceForm }.toTypedArray()
            val flattenTermEntry = termEntries.flatMap { listOf(it.leftId, it.rightId, it.cost,
                    it.features.partOfSpeech.ordinal
            )}.toIntArray()

            IOUtils.writeInt(outputStream, size)
            IOUtils.writeIntArray(outputStream, flattenTermEntry, includeSize = false)
            IOUtils.writeStringArray(outputStream, surfaceForms, includeSize = false)
        }
    }
}