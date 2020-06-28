package com.github.wanasit.kotori.benchmark

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.benchmark.dataset.LivedoorNewsDataset
import com.github.wanasit.kotori.benchmark.dataset.TatoebaDataset
import com.github.wanasit.kotori.benchmark.dataset.TextDatasetEntry
import com.github.wanasit.kotori.dictionary.Dictionaries
import com.github.wanasit.kotori.sudachi.dictionary.SudachiDictionary
import com.github.wanasit.kotori.utils.format
import com.github.wanasit.kotori.utils.measureTimeMillisWithOutput
import com.github.wanasit.kotori.utils.measureTimeNanoWithOutput
import com.github.wanasit.kotori.utils.runAndPrintTimeMillis

class Benchmark: CliktCommand() {
    val dataset: String by option().choice("tatoeba", "livedoor-news").default("tatoeba")
    val tokenizer by option().choice("kotori", "kuromoji", "sudachi").default("kotori")
    val dictionary: String? by option().choice("ipadic", "sudachi-small")

    override fun run() {

        val dataset = runAndPrintTimeMillis("Loading [${this.dataset}] dataset") {
            when (this.dataset) {
                "tatoeba" -> TatoebaDataset.loadJapaneseSentences()
                "livedoor-news" -> LivedoorNewsDataset.loadDataset()
                else -> throw UnsupportedOperationException()
            }
        }

        val tokenizer = if (tokenizer == "kotori") {
            val dictionary: Dictionary<TermEntry>? = runAndPrintTimeMillis("Loading [${this.dictionary?:"<default>"}] dictionary") {
                when (this.dictionary) {
                    "ipadic" -> Dictionaries.Mecab.loadIpadic()
                    "sudachi-small" -> SudachiDictionary.readSystemDictionary(Dictionaries.Sudachi.smallDictionaryPath())
                    else -> Dictionary.readDefaultFromResource()
                }
            }

            runAndPrintTimeMillis("Building tokenizer with [${this.dictionary?:"<default>"}] dictionary") {
                Tokenizer.create(dictionary ?: throw java.lang.IllegalStateException())
            }

        } else {
            runAndPrintTimeMillis("Loading [${this.tokenizer}] tokenizer") {
                when (this.tokenizer) {
                    "kuromoji" -> Tokenizers.loadKuromojiIpadicTokenizer()
                    "sudachi" -> Tokenizers.loadSudachiTokenizer()
                    else -> throw UnsupportedOperationException()
                }
            }
        }

        runBenchmark(tokenizer, dataset)
    }
}

fun runBenchmark(tokenizer: Tokenizer, dataset: Collection<TextDatasetEntry>) {
    println("Benchmarking ${tokenizer} with ${dataset.size.format()} text entries " +
            "(${dataset.map { it.text.length }.sum().format()} total characters)" )

    val (warmUpTimeMillis, warmUpTokenCount) = measureTimeMillisWithOutput {
        runCountToken(tokenizer, dataset, 3);
    }

    println("Finished warming up: ${warmUpTimeMillis.format()} ms (${warmUpTokenCount.format()} tokens extracted)")

    val recordedPerToken = mutableListOf<Long>()
    val recordedPerDocument = mutableListOf<Long>()
    for (epoch in 1..10) {
        val (time, tokenCount) = measureTimeNanoWithOutput { runCountToken(tokenizer, dataset); }

        val perToken = time / tokenCount
        val perDocument = time / dataset.size
        println("Benchmark epoch ${epoch.format("%2d")}: ${perDocument.format("%6d")} ns per document " +
                "(${tokenCount.format()} tokens extracted, ${perToken.format("%4d")} ns per token)")
        recordedPerToken.add(perToken)
        recordedPerDocument.add(perDocument)
    }

    println("Averge: ${recordedPerDocument.average()} ns per document")
    println("Averge: ${recordedPerToken.average()} ns per token")
}

fun runCountToken(tokenizer: Tokenizer,
                  dataset: Collection<TextDatasetEntry>,
                  epoch: Int = 1) : Int {

    var totalTokenCount = 0;
    for (i in 0 until epoch) {
        totalTokenCount += dataset.map { tokenizer.tokenize(it.text).size }.sum();
    }

    return totalTokenCount;
}

fun main(args: Array<String>) = Benchmark().main(args)