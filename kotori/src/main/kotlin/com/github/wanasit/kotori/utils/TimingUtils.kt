package com.github.wanasit.kotori.utils

inline fun <Output> measureTimeMillisWithOutput(block: () -> Output): Pair<Long, Output> {
    val start = System.currentTimeMillis()
    val output = block()
    return System.currentTimeMillis() - start to output
}

inline fun <Output> measureTimeNanoWithOutput(block: () -> Output): Pair<Long, Output> {
    val start = System.nanoTime()
    val output = block()
    return System.nanoTime() - start to output
}

inline fun <Output> runAndPrintTimeMillis(msg: String, block: () -> Output) : Output {
    val (time, output) = measureTimeMillisWithOutput { block() }
    println("[$msg] took $time ms")
    return output;
}