package com.github.wanasit.kotori.dictionary

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.mecab.MeCabDictionary
import com.github.wanasit.kotori.mecab.MeCabTermEntry

object Dictionaries {
    const val SudachiDictVersion = "sudachi-dictionary-20200330"
    const val MecabIpadicVersion = "mecab-ipadic-2.7.0-20070801"

    const val MecabIpadicDataDirectory = "../data/$MecabIpadicVersion"

    fun loadMecabIpadic(): Dictionary<MeCabTermEntry> {
        return MeCabDictionary.readFromDirectory(MecabIpadicDataDirectory)
    }
}