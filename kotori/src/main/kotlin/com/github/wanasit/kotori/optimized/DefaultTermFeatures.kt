package com.github.wanasit.kotori.optimized

data class DefaultTermFeatures(
    val partOfSpeech: PartOfSpeech = PartOfSpeech.UNKNOWN
) {

    enum class PartOfSpeech(vararg val labels: String) {
        ADJECTIVE("形容詞"),
        ADNOMINAL("連体詞"),
        ADVERB("副詞"),
        AUXILIARY("助動詞"),
        CONJUNCTION("接続詞"),
        INTERJECTION("感動詞", "フィラー"),
        NOUN("名詞"),
        PARTICLE("助詞"),
        PREFIX("接頭詞"),
        SUFFIX("(名詞)接尾"),
        SYMBOL("記号"),
        VERB("動詞"),

        OTHER(),
        UNKNOWN()
    }
}