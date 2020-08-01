package com.github.wanasit.kotori.benchmark.tries

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.wanasit.kotori.benchmark.dataset.loadDatasetByName
import com.github.wanasit.kotori.benchmark.dictionary.loadDictionaryByName
import com.github.wanasit.kotori.optimized.tries.TransitionArrayTrie
import com.github.wanasit.kotori.utils.format
import com.github.wanasit.kotori.utils.measureTimeNanoWithOutput
import com.github.wanasit.kotori.utils.runAndPrintTimeMillis
import com.github.wanasit.kotori.utils.termEntries

class BenchmarkPatternMatching: CliktCommand() {
    val dictionary: String by option().choice("ipadic", "sudachi-small")
            .default("ipadic")
    val dataset: String by option().choice("tatoeba", "livedoor-news")
            .default("livedoor-news")

    val implementation: String by option().choice("hashmap-trie", "transition-array-trie")
            .default("transition-array-trie")

    private val IMPLEMENTATIONS: Map<String, (List<String>) -> DFAWithTerminalState> = mapOf(

            "hashmap-trie" to { terms: List<String> ->
                val (trie, terminalStatesLookup) = createHashMapTrieAndIndexTerminalStates(terms)
                DFAWithTerminalState(trie, terminalStatesLookup)
            },

            "transition-array-trie" to { terms: List<String> ->
                val (trie, terminalStatesLookup) = createHashMapTrieAndIndexTerminalStates(terms)
                DFAWithTerminalState(TransitionArrayTrie(trie), terminalStatesLookup)
            }
    )


    override fun run() {
        val dataset = loadDatasetByName(this.dataset).map { it.text.toCharArray() }

        val dictionary = loadDictionaryByName(this.dictionary)
        val terms = dictionary.termEntries.map { it.surfaceForm }

        val trieImplementation = IMPLEMENTATIONS[this.implementation] ?:
            throw IllegalArgumentException("Unknown implementation ${this.implementation}")


        val trie = runAndPrintTimeMillis("Building Trie of [${implementation}] implementation") {
            trieImplementation(terms)
        }

        runAndPrintTimeMillis("Warming up") {
            runCountToken(trie, dataset);
        }

        val recordedPerTermFound = mutableListOf<Double>()
        val recordedPerDocument = mutableListOf<Double>()
        for (epoch in 1..10) {
            val (time, termCount) = measureTimeNanoWithOutput { runCountToken(trie, dataset); }

            val perTerm = time / termCount.toDouble()
            val perDocument = time / dataset.size.toDouble()
            println("Benchmark epoch ${epoch.format("%2d")}: ${perDocument.format("%7.2f")} ns per document " +
                    "(${termCount.format()} terms found, ${perTerm.format("%5.2f")} ns per term)")
            recordedPerDocument.add(perDocument)
            recordedPerTermFound.add(perTerm)
        }

        println("Averge: ${recordedPerDocument.average().format("%7.2f")} ns per document")
        println("Averge: ${recordedPerTermFound.average().format("%5.2f")} ns per term")
    }

    private fun runCountToken(trie: DFAWithTerminalState, dataset: Collection<CharArray>) : Int {
        var termCount = 0;
        for (text in dataset) {
            val length = text.size
            var i = 0
            while (i < length) {
                trie.findTermsStartingAtIndex(text, i++) {
                    termCount += 1
                }
            }
        }

        return termCount
    }
}

fun main(args: Array<String>) = BenchmarkPatternMatching().main(args)