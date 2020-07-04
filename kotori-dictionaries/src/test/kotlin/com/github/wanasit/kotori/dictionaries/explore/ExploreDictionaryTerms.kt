package com.github.wanasit.kotori.dictionaries.explore

import com.github.wanasit.kotori.dictionaries.Dictionaries
import com.github.wanasit.kotori.utils.termEntries


fun main() {

    val dictionary = Dictionaries.Mecab.loadUnidic()

    with(dictionary.termEntries.groupingBy { it.features.partOfSpeech }.eachCount()) {
        println("partOfSpeech: ${this.size} distinct, ${this.entries.sortedBy { -it.value }.take(20)}")
    }

    with(dictionary.termEntries.groupingBy { it.features.partOfSpeechSubCategory1 }.eachCount()) {
        println("partOfSpeechSubCategory1: ${this.size} distinct, ${this.entries.sortedBy { -it.value }.take(20)}")
    }

    with(dictionary.termEntries.groupingBy { it.features.partOfSpeechSubCategory2 }.eachCount()) {
        println("partOfSpeechSubCategory2: ${this.size} distinct, ${this.entries.sortedBy { -it.value }.take(20)}")
    }

    with(dictionary.termEntries.groupingBy { it.features.partOfSpeechSubCategory3 }.eachCount()) {
        println("partOfSpeechSubCategory3: ${this.size} distinct, ${this.entries.sortedBy { -it.value }.take(20)}")
    }

    with(dictionary.termEntries.groupingBy { it.features.conjugationForm }.eachCount()) {
        println("conjugationForm: ${this.size} distinct, ${this.entries.sortedBy { -it.value }.take(20)}")
    }

    with(dictionary.termEntries.groupingBy { it.features.conjugationType }.eachCount()) {
        println("conjugationType: ${this.size} distinct, ${this.entries.sortedBy { -it.value }.take(20)}")
    }
}
