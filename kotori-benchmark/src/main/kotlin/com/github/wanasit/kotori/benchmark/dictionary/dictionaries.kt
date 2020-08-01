package com.github.wanasit.kotori.benchmark.dictionary

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.dictionaries.Dictionaries
import com.github.wanasit.kotori.sudachi.dictionary.SudachiDictionary
import com.github.wanasit.kotori.utils.runAndPrintTimeMillis


fun loadDictionaryByName(name: String): Dictionary<*> {

    return runAndPrintTimeMillis("Loading [${name}] dictionary") {
        when (name) {
            "ipadic" -> Dictionaries.Mecab.loadIpadic()
            "sudachi-small" -> SudachiDictionary.readSystemDictionary(Dictionaries.Sudachi.smallDictionaryPath())
            "default" -> Dictionary.readDefaultFromResource()

            else -> throw UnsupportedOperationException("Unknown dictionary '$name'")
        }
    }
}