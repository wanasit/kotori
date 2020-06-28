package com.github.wanasit.kotori.benchmark.dataset

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
