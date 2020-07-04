package com.github.wanasit.kotori.mecab

import com.github.wanasit.kotori.ConnectionCost
import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.TermDictionary
import com.github.wanasit.kotori.optimized.PlainConnectionCostTable
import com.github.wanasit.kotori.optimized.PlainTermDictionary
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
                ?.flatMap { MeCabTermFeatures.readTermEntriesFromFileInputStream(it.inputStream(), charset=charset) }
                ?: throw IllegalArgumentException("Can't read dictionary files in $dir")

        return PlainTermDictionary(dictionaryEntries.toTypedArray())
    }

    fun readFromInputStream(inputStream: InputStream, charset: Charset) : TermDictionary<MeCabTermFeatures> {
        val dictionaryEntries = MeCabTermFeatures.readTermEntriesFromFileInputStream(inputStream, charset)
        return PlainTermDictionary(dictionaryEntries.toTypedArray())
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
        val array = PlainConnectionCostTable(fromIdCardinality, toIdCardinality)

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