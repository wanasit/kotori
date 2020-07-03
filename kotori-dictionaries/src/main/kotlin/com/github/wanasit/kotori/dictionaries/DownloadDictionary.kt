package com.github.wanasit.kotori.dictionaries

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class DownloadDictionary: CliktCommand() {
    override fun run() = Unit

    internal class MecabIpadic: CliktCommand() {
        override fun run() {
            Dictionaries.Mecab.downloadIPADic()
        }
    }

    internal class MecabUnidic: CliktCommand() {
        override fun run() {
            Dictionaries.Mecab.downloadUniDic()
        }
    }

    internal class SudachiSmall: CliktCommand() {
        override fun run() {
            Dictionaries.Sudachi.downloadSmallDictionary()
        }
    }
    internal class SudachiCore: CliktCommand() {
        override fun run() {
            Dictionaries.Sudachi.downloadCoreDictionary()
        }
    }
    internal class SudachiFull: CliktCommand() {
        override fun run() {
            Dictionaries.Sudachi.downloadFullDictionary()
        }
    }
}

fun main(args: Array<String>) = DownloadDictionary()
        .subcommands(
                DownloadDictionary.MecabIpadic(),
                DownloadDictionary.MecabUnidic(),
                DownloadDictionary.SudachiSmall(),
                DownloadDictionary.SudachiCore(),
                DownloadDictionary.SudachiFull()
        ).main(args)
