package com.github.wanasit.kotori.dictionary

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.benchmark.Benchmark
import com.github.wanasit.kotori.optimized.dictionary.OptimizedDictionary
import java.io.File
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


fun main() {

    val sourceDictionary = Benchmark.runAndPrintTimeMillis("Loading source dictionary") {
        Dictionaries.loadMecabIpadic()
    }

    val optimizedDictionary = Benchmark.runAndPrintTimeMillis("Building target dictionary") {
        OptimizedDictionary.copyOf(sourceDictionary)
    }

    val targetFilename = "../kotori/src/main/resources/default_dictionary.bin.gz"
    Benchmark.runAndPrintTimeMillis("Writing target dictionary") {
        File(targetFilename).outputStream().use {
            GZIPOutputStream(it).use {
                optimizedDictionary.writeToOutputStream(it)
            }
        }
    }

    val writtenDictionary = Benchmark.runAndPrintTimeMillis("Reading written dictionary") {
        File(targetFilename).inputStream().use {
            GZIPInputStream(it).use {
                OptimizedDictionary.readFromInputStream(it)
            }
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

fun printTokenizeResultsComparision(tokenizerSource: Tokenizer, tokenizerTarget: Tokenizer, text: String) {

    println("Tokenize '$text'")
    println("> ${tokenizerSource.tokenize(text).map { it.text }}")
    println("> ${tokenizerTarget.tokenize(text).map { it.text }}")
}