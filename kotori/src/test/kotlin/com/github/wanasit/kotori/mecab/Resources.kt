package com.github.wanasit.kotori.mecab

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.utils.ResourceUtil
import java.nio.charset.Charset

const val DEFAULT_RESOURCE_NAMESPACE: String = "/mecab_ipadic_dict"
const val FILE_NAME_TERM_DICTIONARY = "Adverb.csv"

fun MeCabDictionary.readFromResource(
        namespace: String = DEFAULT_RESOURCE_NAMESPACE,
        charset: Charset = MeCabDictionary.DEFAULT_CHARSET
) : Dictionary<MeCabTermEntry> {

    val termDictionary = MeCabTermDictionary.readFromInputStream(
            ResourceUtil.readResourceAsStream(namespace, FILE_NAME_TERM_DICTIONARY), charset)

    val termConnection = MeCabConnectionCost.readFromInputStream(
            ResourceUtil.readResourceAsStream(namespace, MeCabDictionary.FILE_NAME_CONNECTION_COST), charset)

    val unknownTermStrategy = MeCabUnknownTermExtractionStrategy.readFromFileInputStreams(
            ResourceUtil.readResourceAsStream(namespace, MeCabDictionary.FILE_NAME_UNKNOWN_ENTRIES),
            ResourceUtil.readResourceAsStream(namespace, MeCabDictionary.FILE_NAME_CHARACTER_DEFINITION),
            charset)

    return Dictionary(
            termDictionary,
            termConnection,
            unknownTermStrategy
    )
}