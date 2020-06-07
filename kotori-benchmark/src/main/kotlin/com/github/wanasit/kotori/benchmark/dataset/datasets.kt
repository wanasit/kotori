package com.github.wanasit.kotori.benchmark.dataset

import com.beust.klaxon.Klaxon
import java.nio.file.Paths

interface TextDatasetEntry {
    val text: String
}

typealias Dataset<T> = Collection<T>


object LivedoorNews{
    data class LivedoorNewsEntry(
            val url: String,
            val title: String,
            val body: String): TextDatasetEntry{
        override val text = body;
    }

    fun loadDataset(dataDir:String = "../data/livedoor-news") : Dataset<LivedoorNewsEntry> {
        val file = Paths.get(dataDir).resolve("topic-news.json").toFile();
        val dataset = Klaxon().parseArray<LivedoorNewsEntry>(file)

        return dataset ?: throw IllegalArgumentException();
    }
}

fun <T> Dataset<T>.repeat(times: Int = 1): Dataset<T>{
    val newRepeatedDataset = this.toMutableList()
    repeat(times) {
        newRepeatedDataset.addAll(this)
    }
    return newRepeatedDataset
}

