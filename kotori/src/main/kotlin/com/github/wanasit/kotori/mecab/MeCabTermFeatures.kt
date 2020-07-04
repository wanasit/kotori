package com.github.wanasit.kotori.mecab

import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.optimized.PlainTermEntry
import com.github.wanasit.kotori.utils.CSVUtil
import java.io.InputStream
import java.nio.charset.Charset

typealias MeCabLikeTermFeatures = MeCabTermFeatures
typealias MeCabTermEntry = TermEntry<MeCabTermFeatures>

/**
 * Ref: http://taku910.github.io/mecab/dic.html
 */
class MeCabTermFeatures(
        val partOfSpeech: String? = null,
        val partOfSpeechSubCategory1: String? = null,
        val partOfSpeechSubCategory2: String? = null,
        val partOfSpeechSubCategory3: String? = null,
        val conjugationType: String? = null,
        val conjugationForm: String? = null) {

    companion object {

        fun readTermEntriesFromFileInputStream(inputStream: InputStream, charset: Charset): List<TermEntry<MeCabTermFeatures>> {
            return inputStream.reader(charset = charset)
                    .readLines()
                    .map { CSVUtil.parseLine(it) }
                    .map { parseTermEntryFromValue(it) }
        }

        fun parseTermEntryFromValue(values: List<String>): TermEntry<MeCabTermFeatures> {
            return PlainTermEntry(
                    surfaceForm = values[0],
                    leftId = values[1].toInt(),
                    rightId = values[2].toInt(),
                    cost = values[3].toInt(),
                    features = MeCabTermFeatures(
                            partOfSpeech = values[4],
                            partOfSpeechSubCategory1 = values[5],
                            partOfSpeechSubCategory2 = values[6],
                            partOfSpeechSubCategory3 = values[7],
                            conjugationType = values[8],
                            conjugationForm = values[9])
            )
        }
    }
}