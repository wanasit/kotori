package com.github.wanasit.kotori.mecab

import com.github.wanasit.kotori.utils.size
import org.junit.Test
import kotlin.test.assertTrue


class TestMeCabDictionary {

    @Test fun loadAndUseDefaultDictionaryFromResource() {
        val dictionary = MeCabDictionary.readFromResource()
        assertTrue(dictionary.size > 0)
    }

}