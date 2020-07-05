package com.github.wanasit.kotori.mecab

import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.optimized.PlainTermEntry
import com.github.wanasit.kotori.utils.CSVUtil
import java.io.InputStream
import java.nio.charset.Charset

typealias MeCabTermFeatures = MeCabLikeTermFeatures
typealias MeCabTermEntry = TermEntry<MeCabTermFeatures>
typealias ParseFeatures = (values: List<String>) -> MeCabTermFeatures

/**
 * Ref: http://taku910.github.io/mecab/dic.html
 */
class MeCabLikeTermFeatures(
        val partOfSpeech: String? = null,
        val partOfSpeechSubCategory1: String? = null,
        val partOfSpeechSubCategory2: String? = null,
        val partOfSpeechSubCategory3: String? = null,
        val conjugationType: String? = null,
        val conjugationForm: String? = null) {

    companion object {

        fun readTermEntriesFromFileInputStream(
                inputStream: InputStream,
                charset: Charset,
                parseFeatures: ParseFeatures = ::parseWithStandardValueOrder
        ): List<TermEntry<MeCabLikeTermFeatures>> {
            return inputStream.reader(charset = charset)
                    .readLines()
                    .map { CSVUtil.parseLine(it) }
                    .map { parseTermEntryFromValues(it, parseFeatures) }
        }

        fun parseTermEntryFromValues(
                values: List<String>,
                parseFeatures: ParseFeatures = ::parseWithStandardValueOrder
        ): TermEntry<MeCabLikeTermFeatures> {
            return PlainTermEntry(
                    surfaceForm = values[0],
                    leftId = values[1].toInt(),
                    rightId = values[2].toInt(),
                    cost = values[3].toInt(),
                    features = parseFeatures(values.subList(4, values.size))
            )
        }

        private fun parseWithStandardValueOrder(values: List<String>): MeCabLikeTermFeatures {
            val nullableValues = values.map { if (it == "*") null else it }
            return MeCabTermFeatures(
                    partOfSpeech = nullableValues[0],
                    partOfSpeechSubCategory1 = nullableValues[1],
                    partOfSpeechSubCategory2 = nullableValues[2],
                    partOfSpeechSubCategory3 = nullableValues[3],
                    conjugationType = nullableValues[4],
                    conjugationForm = nullableValues[5]
            )
        }
    }
}