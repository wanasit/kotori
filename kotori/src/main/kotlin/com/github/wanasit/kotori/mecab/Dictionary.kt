package com.github.wanasit.kotori.mecab

import com.github.wanasit.kotori.utils.CSVUtil
import com.github.wanasit.kotori.utils.ResourceUtil.readResourceAsStream
import com.github.wanasit.kotori.utils.checkArgument
import com.github.wanasit.kotori.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

/**
 *
 */
object MeCabDictionary {

    const val DEFAULT_RESOURCE_NAMESPACE: String = "/mecab_ipadic_dict"
    const val FILE_NAME_CONNECTION_COST = "matrix.def";
    const val FILE_NAME_UNKNOWN_ENTRIES = "unk.def";
    const val FILE_NAME_CHARACTER_DEFINITION = "char.def";
    const val FILE_NAME_TERM_DICTIONARY = "terms.csv";

    val DEFAULT_CHARSET: Charset = Charset.forName("EUC-JP")

    fun readFromDirectory(
            dir: String,
            charset: Charset = DEFAULT_CHARSET
    ) : Dictionary<MeCabTermEntry> {

        val dictionaryDir = Path.of(dir);
        checkArgument(Files.isDirectory(dictionaryDir));

        val termDictionary = MeCabTermDictionary.readFromDirectory(dir, charset);

        val termConnection = MeCabConnectionCost.readFromInputStream(
                dictionaryDir.resolve(FILE_NAME_CONNECTION_COST).toFile().inputStream(),
                charset = DEFAULT_CHARSET)

        val unknownTermDictionary = MeCabUnknownTermExtractionStrategy.readFromDirectory(dir, charset)

        return Dictionary(
                termDictionary,
                termConnection,
                unknownTermDictionary
        );
    }

    fun readFromResource(
            namespace: String = DEFAULT_RESOURCE_NAMESPACE,
            charset: Charset = DEFAULT_CHARSET
    ) : Dictionary<MeCabTermEntry> {

        val termDictionary = MeCabTermDictionary.readFromInputStream(
                readResourceAsStream(namespace, FILE_NAME_TERM_DICTIONARY), charset);

        val termConnection = MeCabConnectionCost.readFromInputStream(
                readResourceAsStream(namespace, FILE_NAME_CONNECTION_COST), charset)

        val unknownTermStrategy = MeCabUnknownTermExtractionStrategy.readFromInputStream(
                readResourceAsStream(namespace, FILE_NAME_UNKNOWN_ENTRIES),
                readResourceAsStream(namespace, FILE_NAME_CHARACTER_DEFINITION),
                charset)

        return Dictionary(
                termDictionary,
                termConnection,
                unknownTermStrategy
        )
    }
}

class MeCabTermDictionary(
        private val entryList: List<MeCabTermEntry>
) : TermDictionary<MeCabTermEntry> {

    companion object {
        fun readFromDirectory(
                dir: String,
                charset: Charset
        ) : MeCabTermDictionary {
            val dictionaryEntries = File(dir).listFiles()
                    ?.filter { it.isFile && it.name.endsWith("csv") }
                    ?.sortedBy { it.name }
                    ?.flatMap { MeCabTermEntry.read(it.inputStream(), charset=charset) }
                    ?: throw IllegalArgumentException("Can't read dictionary files in $dir")

            return MeCabTermDictionary(dictionaryEntries);
        }

        fun readFromInputStream(inputStream: InputStream, charset: Charset) : MeCabTermDictionary{
            val dictionaryEntries = MeCabTermEntry.read(inputStream, charset)
            return MeCabTermDictionary(dictionaryEntries);
        }
    }

    override fun get(id: Int): MeCabTermEntry? {
        return entryList[id];
    }

    override fun iterator(): Iterator<Pair<TermID, MeCabTermEntry>> {
        return entryList.mapIndexed { i, entry ->  i to entry}.iterator()
    }
}

class MeCabTermEntry(
        override val surfaceForm: String,
        override val leftId: Int,
        override val rightId: Int,
        override val cost: Int
) : TermEntry {

    companion object {

        fun read(inputStream: InputStream, charset: Charset) : List<MeCabTermEntry> {
            return inputStream.reader(charset = charset)
                    .readLines()
                    .map { parseLine(it) }
        }

        fun write(entries: List<MeCabTermEntry>, outputStream: OutputStream, charset: Charset) {
            return PrintWriter(outputStream.writer(charset)).use { printer ->
                entries.forEach {
                    printer.println(writeLine(it))
                }
            }
        }

        private fun parseLine(line: String): MeCabTermEntry{
            val values = CSVUtil.parseLine(line)
            return MeCabTermEntry(
                    surfaceForm = values[0],
                    leftId = values[1].toInt(),
                    rightId = values[2].toInt(),
                    cost = values[3].toInt()
            )
        }

        private fun writeLine(entry: MeCabTermEntry) : String {
            return CSVUtil.writeLine(
                    entry.surfaceForm,
                    entry.leftId,
                    entry.rightId,
                    entry.cost
            )
        }
    }
}

class MeCabConnectionCost(
        private val table: Map<Pair<Int, Int>, Int>
) : ConnectionCost {

    override fun lookup(fromRightId: Int, toLeftId: Int): Int? {
        return this.table[fromRightId to toLeftId]
    }

    companion object {

        fun readFromInputStream(inputStream: InputStream, charset: Charset) : MeCabConnectionCost {
            return readFromInputStream(inputStream
                    .reader(charset = charset)
                    .readLines())
        }

        private fun readFromInputStream(lines: List<String>) : MeCabConnectionCost {

            val table: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()
            val whiteSpaceRegEx = "\\s+".toRegex()

            lines.drop(1)
                    .forEach {

                val values = whiteSpaceRegEx.split(it)

                val fromId = values[0].toInt()
                val toId = values[1].toInt()
                val cost = values[2].toInt()
                table[fromId to toId] = cost
            }

            return MeCabConnectionCost(table);
        }
    }
}
