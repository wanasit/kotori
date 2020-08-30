package com.github.wanasit.kotori.dictionaries

import com.github.wanasit.kotori.utils.termEntries
import org.junit.Test
import kotlin.test.assertTrue


class TestDictionaries {

    @Test fun testLoadingMeCabIpadic() {
        val dictionary = Dictionaries.Mecab.loadIpadic()
        assertTrue(dictionary.terms.size() > 0)
    }

    @Test fun testLoadingMeCabUnidic() {
        val dictionary = Dictionaries.Mecab.loadUnidic()
        assertTrue(dictionary.terms.size() > 0)
    }
}