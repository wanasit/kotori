package com.github.wanasit.kotori.benchmark

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.wanasit.kotori.benchmark.dataset.LivedoorNewsDataset
import com.github.wanasit.kotori.benchmark.dataset.TatoebaDataset

class DownloadDataset: CliktCommand() {
    override fun run() = Unit

    internal class Tatoeba: CliktCommand(help="Download Tatoeba dataset") {
        override fun run() {
            TatoebaDataset.download()
        }
    }

    class LivedoorNews: CliktCommand(help="Download LivedoorNews dataset") {
        override fun run() {
            LivedoorNewsDataset.download()
        }
    }
}

fun main(args: Array<String>) = DownloadDataset()
        .subcommands(
                DownloadDataset.Tatoeba(),
                DownloadDataset.LivedoorNews()
        ).main(args)