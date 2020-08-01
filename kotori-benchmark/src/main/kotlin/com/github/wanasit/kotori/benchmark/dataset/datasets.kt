package com.github.wanasit.kotori.benchmark.dataset

import com.github.wanasit.kotori.utils.runAndPrintTimeMillis

interface TextDatasetEntry {
    val text: String
}

typealias Dataset<T> = Collection<T>

fun <T> Dataset<T>.repeat(times: Int = 1): Dataset<T>{
    val newRepeatedDataset = this.toMutableList()
    repeat(times) {
        newRepeatedDataset.addAll(this)
    }
    return newRepeatedDataset
}


fun loadDatasetByName(name: String): Dataset<out TextDatasetEntry> {
    return runAndPrintTimeMillis("Loading [${name}] dataset") {
            when (name) {
                "tatoeba" -> TatoebaDataset.loadJapaneseSentences()
                "livedoor-news" -> LivedoorNewsDataset.loadDataset()
                else -> throw UnsupportedOperationException()
            }
        }
}