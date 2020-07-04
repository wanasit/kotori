package com.github.wanasit.kotori.mecab

typealias MeCabLikeTermFeatures = MeCabTermFeatures

class MeCabTermFeatures(
        val partOfSpeech: String? = null,
        val partOfSpeechSubCategory1: String? = null,
        val partOfSpeechSubCategory2: String? = null,
        val partOfSpeechSubCategory3: String? = null,
        val conjugationType: String? = null,
        val conjugationForm: String? = null)