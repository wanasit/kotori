package com.github.wanasit.kotori.benchmark.dataset

import com.github.wanasit.kotori.dictionaries.utils.downloadIntoDirectory
import com.github.wanasit.kotori.dictionaries.utils.extractIntoDirectory
import com.github.wanasit.kotori.utils.CSVUtil
import java.io.File


/**
 * Tatoeba's Sentences Dataset
 * From: https://tatoeba.org/
 *
 * License: Creative commons. The files are released under CC BY 2.0 FR.
 */
object TatoebaDataset {

    const val DEFAULT_DATA_DIR = "../data/tatoeba/"
    private const val FILE_DOWNLOAD_URL = "https://downloads.tatoeba.org/exports/sentences.tar.bz2"
    private const val FILENAME = "sentences.csv"

    data class TatoebaSentencEntry(
            val language: String,
            val sentence: String): TextDatasetEntry{
        override val text = sentence
    }

    fun loadJapaneseSentences(dataDir:String = DEFAULT_DATA_DIR) : Dataset<TatoebaSentencEntry> {
        return loadSentencesAsSequence(dataDir).filter {
            it.language == "jpn"
        }.toList()
    }

    private fun loadSentencesAsSequence(dataDir:String = DEFAULT_DATA_DIR) : Sequence<TatoebaSentencEntry> {
        /**
         * 1	cmn	我們試試看！
         * 2	cmn	我该去睡觉了。
         * ...
         */
        return File(File(dataDir), FILENAME).readLines()
                .asSequence()
                .map { CSVUtil.parseLine(it, separator = '\t', quote = null) }
                .map { TatoebaSentencEntry(it[1], it[2]) }
    }

    fun download(
            dataDir: String = DEFAULT_DATA_DIR,
            overwrite: Boolean = false
    ) {
        val dataDirFile = File(dataDir)
        dataDirFile.mkdirs()

        if (File(dataDirFile, FILENAME).exists() && !overwrite) {
            return
        }

        val downloadedFile = downloadIntoDirectory(dataDirFile, FILE_DOWNLOAD_URL)
        extractIntoDirectory(dataDirFile, downloadedFile)
    }
}