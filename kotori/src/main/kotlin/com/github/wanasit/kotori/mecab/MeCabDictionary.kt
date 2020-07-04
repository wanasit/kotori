package com.github.wanasit.kotori.mecab

import com.github.wanasit.kotori.*
import com.github.wanasit.kotori.optimized.DefaultConnectionCost
import com.github.wanasit.kotori.optimized.DefaultTermDictionary
import com.github.wanasit.kotori.utils.CSVUtil
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 */
object MeCabDictionary {

    const val FILE_NAME_CONNECTION_COST = "matrix.def"
    const val FILE_NAME_UNKNOWN_ENTRIES = "unk.def"
    const val FILE_NAME_CHARACTER_DEFINITION = "char.def"

    val DEFAULT_CHARSET: Charset = Charset.forName("EUC-JP")

    fun readFromDirectory(
            dir: String,
            charset: Charset = DEFAULT_CHARSET
    ) : Dictionary<MeCabTermFeatures> {

        val dictionaryDir = Paths.get(dir)
        check(Files.isDirectory(dictionaryDir))

        val termDictionary = MeCabTermDictionary.readFromDirectory(dir, charset)
        val termConnection = MeCabConnectionCost.readFromInputStream(
                dictionaryDir.resolve(FILE_NAME_CONNECTION_COST).toFile().inputStream(),
                charset = DEFAULT_CHARSET)

        val unknownTermDictionary = MeCabUnknownTermExtractionStrategy.readFromDirectory(dir, charset)

        return Dictionary(
                termDictionary,
                termConnection,
                unknownTermDictionary
        )
    }
}

object MeCabTermDictionary {

    fun readFromDirectory(
            dir: String,
            charset: Charset
    ) : TermDictionary<MeCabTermFeatures> {
        val dictionaryEntries = File(dir).listFiles()
                ?.filter { it.isFile && it.name.endsWith("csv") }
                ?.sortedBy { it.name }
                ?.flatMap { MeCabTermEntry.readEntriesFromFileInputStream(it.inputStream(), charset=charset) }
                ?: throw IllegalArgumentException("Can't read dictionary files in $dir")

        return DefaultTermDictionary(dictionaryEntries.toTypedArray())
    }

    fun readFromInputStream(inputStream: InputStream, charset: Charset) : TermDictionary<MeCabTermFeatures> {
        val dictionaryEntries = MeCabTermEntry.readEntriesFromFileInputStream(inputStream, charset)
        return DefaultTermDictionary(dictionaryEntries.toTypedArray())
    }
}

/**
 * Ref: http://taku910.github.io/mecab/dic.html
 */
data class MeCabTermEntry(
        override val surfaceForm: String,
        override val leftId: Int,
        override val rightId: Int,
        override val cost: Int,
        override val features: MeCabTermFeatures
) : TermEntry<MeCabTermFeatures> {

    companion object {

        fun readEntriesFromFileInputStream(inputStream: InputStream, charset: Charset) : List<MeCabTermEntry> {
            return inputStream.reader(charset = charset)
                    .readLines()
                    .map { parseLine(it) }
        }

        private fun parseLine(line: String): MeCabTermEntry{
            val values = CSVUtil.parseLine(line)
            return MeCabTermEntry(
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

object MeCabConnectionCost {

    fun readFromInputStream(inputStream: InputStream, charset: Charset) : ConnectionCost {
        return readFromInputStream(inputStream
                .reader(charset = charset)
                .readLines())
    }

    private fun readFromInputStream(lines: List<String>) : ConnectionCost {
        val whiteSpaceRegEx = "\\s+".toRegex()

        val cardinality = whiteSpaceRegEx.split(lines.get(0))
        val fromIdCardinality = cardinality[0].toInt()
        val toIdCardinality = cardinality[1].toInt()
        val array = DefaultConnectionCost(fromIdCardinality, toIdCardinality)

        lines.drop(1)
                .forEach {

                    val values = whiteSpaceRegEx.split(it)

                    val fromId = values[0].toInt()
                    val toId = values[1].toInt()
                    val cost = values[2].toInt()
                    array.put(fromId, toId, cost)
                }

        return array
    }
}