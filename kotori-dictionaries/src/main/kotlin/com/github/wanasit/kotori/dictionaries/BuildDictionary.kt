package com.github.wanasit.kotori.dictionaries

import com.github.wanasit.kotori.AnyTokenizer
import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.mecab.MeCabTermFeatures
import com.github.wanasit.kotori.optimized.*
import com.github.wanasit.kotori.optimized.unknown.UnknownTermExtractionByCharacterCategory
import java.io.File
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


fun main() {

    val sourceDictionary = Dictionaries.Mecab.loadIpadic()

    val terms = PlainTermDictionary.copyOf(sourceDictionary.terms) {
        PlainTermEntry(it, DefaultTermFeatures())
    }

    val unknownExtraction = UnknownTermExtractionByCharacterCategory.copyOf(
            sourceDictionary.unknownExtraction as UnknownTermExtractionByCharacterCategory<MeCabTermFeatures>
    ) { termEntry ->
        PlainTermEntry(termEntry, DefaultTermFeatures())
    }

    val optimizedDictionary = DefaultDictionary(
            terms, sourceDictionary.connection as PlainConnectionCostTable, unknownExtraction
    )

    val targetFilename = "../kotori/src/main/resources/default_dictionary.bin.gz"
    File(targetFilename).outputStream().use {
        GZIPOutputStream(it).use {
            DefaultDictionary.writeToOutputStream(it, optimizedDictionary)
        }
    }

    val writtenDictionary = File(targetFilename).inputStream().use {
        GZIPInputStream(it).use {
            DefaultDictionary.readFromInputStream(it)
        }
    }

    checkDictionary(sourceDictionary, writtenDictionary)
}

fun checkDictionary(sourceDictionary: Dictionary<*>, dictionary: Dictionary<*>) {

    val tokenizerSource = Tokenizer.create(sourceDictionary)
    val tokenizerTarget = Tokenizer.create(dictionary)

    printTokenizeResultsComparision(tokenizerSource, tokenizerTarget, "そこではなしは終わりになった")
    printTokenizeResultsComparision(tokenizerSource, tokenizerTarget, "GoogleがAndroid向け点字キーボードを発表")
}

fun printTokenizeResultsComparision(tokenizerSource: AnyTokenizer, tokenizerTarget: AnyTokenizer, text: String) {

    println("Tokenize '$text'")
    println("> ${tokenizerSource.tokenize(text).map { it.text }}")
    println("> ${tokenizerTarget.tokenize(text).map { it.text }}")
}