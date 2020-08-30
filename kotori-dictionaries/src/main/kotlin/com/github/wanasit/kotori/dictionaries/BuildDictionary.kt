package com.github.wanasit.kotori.dictionaries

import com.github.wanasit.kotori.AnyTokenizer
import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.mecab.MeCabTermFeatures
import com.github.wanasit.kotori.optimized.*
import com.github.wanasit.kotori.optimized.unknown.UnknownTermExtractionByCharacterCategory
import com.github.wanasit.kotori.utils.format
import com.github.wanasit.kotori.utils.runAndPrintTimeMillis
import com.github.wanasit.kotori.utils.termEntries
import java.io.File
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

const val TARGET_DICTIONARY_FILENAME = "../kotori/src/main/resources/default_dictionary.bin.gz"

fun main() {

    val sourceDictionary = runAndPrintTimeMillis(
            "Loading source dictionary (MeCab IPADict)") {
        Dictionaries.Mecab.loadIpadic()
    }

    val termEntries = sourceDictionary.termEntries;
    val deduplicatedTermEntries = sourceDictionary.termEntries
            .groupBy { listOf(it.surfaceForm, it.leftId, it.rightId) }
            .map {
                it.value.minBy { it.cost } ?: throw IllegalStateException()
            }
    println("Deduplicated term entries from ${termEntries.size} to ${deduplicatedTermEntries.size}")

    val filteredTermEntries = deduplicatedTermEntries
            .filter { isNotNumbers(it) }
            .filter { isNotDate(it) }
            .map { adjustTermCosts(it) }
    println("Filtered term entries from ${deduplicatedTermEntries.size} to ${filteredTermEntries.size}")

    filteredTermEntries.forEach {
        if (it.leftId != it.rightId) {
            throw AssertionError("Unexpected case where leftId != rightId in the source: $it")
        }
    }

    val targetDictionary = runAndPrintTimeMillis("Building target dictionary") {

        val terms = PlainTermDictionary.copyOf(filteredTermEntries) {
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

    val writtenDictionary = runAndPrintTimeMillis(
            "Reading back the written dictionary") {

        File(TARGET_DICTIONARY_FILENAME).inputStream().use {
            GZIPInputStream(it).use {
                DefaultDictionary.readFromInputStream(it)
            }
        }
    }

    checkDictionary(sourceDictionary, writtenDictionary)
}

fun isNotNumbers(term: TermEntry<*>) : Boolean {

    if (term.surfaceForm.matches(Regex("^[0-9０-９]+$"))) {
        return false
    }

    return true
}

fun isNotDate(term: TermEntry<*>) : Boolean {

    if (term.surfaceForm.matches(Regex("^([0-9０-９]+[年月日])+$"))) {
        return false
    }

    return true;
}

/**
 * As we filter terms which isNotDate(), e.g. "３月", we need to adjust the cost for some suffix
 */
fun <T> adjustTermCosts(term: TermEntry<T>) : TermEntry<T> {

    // 月,1300,1300,17099,名詞,接尾,助数詞,*,*,*,月,ツキ,ツキ
    if (term.surfaceForm == "月" && term.leftId == 1300 && term.rightId == 1300) {
        return PlainTermEntry(term.surfaceForm, term.leftId, term.rightId, term.cost/2, term.features)
    }

    return term;
}


fun checkDictionary(sourceDictionary: Dictionary<*>, dictionary: DefaultDictionary) {

    val tokenizerSource = Tokenizer.create(sourceDictionary)
    val tokenizerTarget = Tokenizer.create(dictionary)

    printTokenizeResultsComparision(tokenizerSource, tokenizerTarget, "そこではなしは終わりになった")
    printTokenizeResultsComparision(tokenizerSource, tokenizerTarget, "GoogleがAndroid向け点字キーボードを発表")
    printTokenizeResultsComparision(tokenizerSource, tokenizerTarget, "２００９年４月２９日に、日本初の直営店としてオープンしたフォーエバー２１原宿店では")
    printTokenizeResultsComparision(tokenizerSource, tokenizerTarget, "2009年4月29日に、日本初の直営店としてオープンしたフォーエバー２１原宿店では")
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