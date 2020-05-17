package com.github.wanasit.kotori.benchmark.dataset

import com.beust.klaxon.Klaxon
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Paths

interface TextDatasetEntry {
    val text: String
}

data class LivedoorNewsEntry(
        val url: String,
        val title: String,
        val body: String): TextDatasetEntry{
    override val text = body;
}

object LivedoorNews{

    fun loadDataset(dataDir:String = "../data/livedoor-news") : Collection<LivedoorNewsEntry> {
        val file = Paths.get(dataDir).resolve("topic-news.json").toFile();
        val dataset = Klaxon().parseArray<LivedoorNewsEntry>(file)

        return dataset ?: throw IllegalArgumentException();
    }
}


