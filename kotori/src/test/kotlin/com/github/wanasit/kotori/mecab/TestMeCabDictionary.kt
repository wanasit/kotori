package com.github.wanasit.kotori.mecab

import com.github.wanasit.kotori.Tokenizer
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class TestMeCabDictionary {

    @Test fun loadAndUseDefaultDictionaryFromResource() {
        val dictionary = MeCabDictionary.readFromResource()
        val tokenizer = Tokenizer.create(dictionary)

        val tokens = tokenizer.tokenize("そこではなしは終わりになった")

        assertNotNull(tokens)
        assertEquals(7, tokens.size)

        assertEquals("そこで", tokens[0].text)
        assertEquals("はなし", tokens[1].text)
        assertEquals("は", tokens[2].text)
        assertEquals("終わり", tokens[3].text)
        assertEquals("に", tokens[4].text)
        assertEquals("なっ", tokens[5].text)
        assertEquals("た", tokens[6].text)
    }

}