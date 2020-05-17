package com.github.wanasit.kotori.benchmark

import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.benchmark.Benchmark.measureTimeMillisWithOutput
import com.github.wanasit.kotori.benchmark.Benchmark.measureTimeNanoWithOutput
import com.github.wanasit.kotori.benchmark.Benchmark.runAndPrintTimeMillis
import com.github.wanasit.kotori.benchmark.dataset.LivedoorNews
import com.github.wanasit.kotori.benchmark.dataset.TextDatasetEntry

object Benchmark {
    public inline fun <Output> measureTimeMillisWithOutput(block: () -> Output): Pair<Long, Output> {
        val start = System.currentTimeMillis()
        val output = block()
        return System.currentTimeMillis() - start to output
    }

    public inline fun <Output> measureTimeNanoWithOutput(block: () -> Output): Pair<Long, Output> {
        val start = System.nanoTime()
        val output = block()
        return System.nanoTime() - start to output
    }

    public inline fun <Output> runAndPrintTimeMillis(msg: String, block: () -> Output) : Output {
        val (time, output) = Benchmark.measureTimeMillisWithOutput { block() }
        println("[$msg] took $time ms")
        return output;
    }
}

// ---------------------------------------------------------

fun main() {
    val dataset = LivedoorNews.loadDataset()
    val sudachi = runAndPrintTimeMillis("Loading Sudachi") {
        Tokenizers.loadSudachiTokenizer();
    }

    val kotori = runAndPrintTimeMillis("Loading Kotori") {
        Tokenizers.loadKotoriTokenizer();
    }

    val kuromoji = runAndPrintTimeMillis("Loading Kuromoji") {
        Tokenizers.loadKuromojiIpadicTokenizer();
    }

    runBenchmark(sudachi, dataset)
    runBenchmark(kuromoji, dataset)
    runBenchmark(kotori, dataset)
}

fun runBenchmark(tokenizer: Tokenizer, dataset: Collection<TextDatasetEntry>) {
    println("----------------------------------------------------------")
    println("Benchmarking ${tokenizer} with ${dataset.size} text entries " +
            "(${dataset.map { it.text.length }.sum()} total characters)" )

    val (warmUpTimeMillis, warmUpTokenCount) = measureTimeMillisWithOutput {
        runCountToken(tokenizer, dataset, 3);
    }

    println("Finished warming up: $warmUpTimeMillis ms ($warmUpTokenCount tokens extracted)")

    for (epoch in 1..4) {
        val (time, tokenCount) = measureTimeNanoWithOutput { runCountToken(tokenizer, dataset); }

        val perToken = time / tokenCount
        println("Benchmark epoch $epoch: $perToken ns per token ($tokenCount tokens extracted)")
    }
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
