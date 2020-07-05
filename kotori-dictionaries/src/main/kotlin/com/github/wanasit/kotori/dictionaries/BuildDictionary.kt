package com.github.wanasit.kotori.dictionaries

import com.github.wanasit.kotori.AnyTokenizer
import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.mecab.MeCabTermFeatures
import com.github.wanasit.kotori.optimized.*
import com.github.wanasit.kotori.optimized.unknown.UnknownTermExtractionByCharacterCategory
import com.github.wanasit.kotori.utils.format
import com.github.wanasit.kotori.utils.runAndPrintTimeMillis
import java.io.File
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

const val TARGET_DICTIONARY_FILENAME = "../kotori/src/main/resources/default_dictionary.bin.gz"

fun main() {

    val sourceDictionary = runAndPrintTimeMillis("Loading source dictionary") {
        Dictionaries.Mecab.loadIpadic()
    }

    val targetDictionary = runAndPrintTimeMillis("Building target dictionary") {

        val terms = PlainTermDictionary.copyOf(sourceDictionary.terms) {
            PlainTermEntry(it, FeatureExtraction.transformFeatures(it.features))
        }

        val unknownExtraction = UnknownTermExtractionByCharacterCategory.copyOf(
                sourceDictionary.unknownExtraction as UnknownTermExtractionByCharacterCategory<MeCabTermFeatures>
        ) {
            PlainTermEntry(it, FeatureExtraction.transformFeatures(it.features))
        }

        DefaultDictionary(
                terms = terms,
                unknownExtraction = unknownExtraction,
                connection = sourceDictionary.connection as PlainConnectionCostTable
        )
    }

    runAndPrintTimeMillis("Writing target dictionary") {
        File(TARGET_DICTIONARY_FILENAME).outputStream().use {
            GZIPOutputStream(it).use {
                DefaultDictionary.writeToOutputStream(it, targetDictionary)
            }
        }
    }

    println("Dictionary file size: ${ (File(TARGET_DICTIONARY_FILENAME).length().toDouble() / 1024).format() } KB")

    val writtenDictionary = File(TARGET_DICTIONARY_FILENAME).inputStream().use {
        GZIPInputStream(it).use {
            DefaultDictionary.readFromInputStream(it)
        }
    }

    checkDictionary(sourceDictionary, writtenDictionary)
}

fun checkDictionary(sourceDictionary: Dictionary<*>, dictionary: DefaultDictionary) {

    val tokenizerSource = Tokenizer.create(sourceDictionary)
    val tokenizerTarget = Tokenizer.create(dictionary)

    printTokenizeResultsComparision(tokenizerSource, tokenizerTarget, "そこではなしは終わりになった")
    printTokenizeResultsComparision(tokenizerSource, tokenizerTarget, "GoogleがAndroid向け点字キーボードを発表")
    printTokenizeResultsComparision(tokenizerSource, tokenizerTarget, "２００９年４月２９日に、日本初の直営店としてオープンしたフォーエバー２１原宿店では")
}

fun printTokenizeResultsComparision(tokenizerSource: AnyTokenizer, tokenizerTarget: Tokenizer<DefaultTermFeatures>, text: String) {
    println("-------------------------------------")
    println("Tokenize '$text'")
    println("Source \t> ${tokenizerSource.tokenize(text).map { it.text }}")
    println("Target \t> ${tokenizerTarget.tokenize(text).map { it.text }}")
    println()
    println("Part-Of-Speech > ${tokenizerTarget.tokenize(text).map { it.features.partOfSpeech }}")
    println()
}