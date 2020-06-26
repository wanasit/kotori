package com.github.wanasit.kotori.sudachi

import com.github.wanasit.kotori.Token
import org.junit.Test
import kotlin.test.assertEquals

class TestCompareWithSudachi {
    private val tokenizer = Sudachi.loadKotoriTokenizerWithSudachiDict()
    private val baseLineTokenizer = Sudachi.loadSudachiTokenizer()


    @Test fun testBasicTokenize() {
        val text = "そこではなしは終わりになった"
        val tokens = tokenizer.tokenize(text)
        val baseLineTokens = baseLineTokenizer.tokenize(text)
        assertTokensEqual(baseLineTokens, tokens)
    }

    @Test fun testWithEnglish() {
        val text = "GoogleがAndroid向け点字キーボードを発表"
        val tokens = tokenizer.tokenize(text)
        val baseLineTokens = baseLineTokenizer.tokenize(text)
        assertTokensEqual(baseLineTokens, tokens)
    }

    @Test fun testWithPunctuations() {
        val text = "...形にとらわれない創作活動も。 \n\n...子は男の子2人。\n"
        val tokens = tokenizer.tokenize(text)
        val baseLineTokens = baseLineTokenizer.tokenize(text)
        assertTokensEqual(baseLineTokens, tokens)
    }

    private fun assertTokensEqual(baseLineTokens: List<Token>, tokens: List<Token>) {
        assertEquals(baseLineTokens.map { it.text }, tokens.map { it.text })
    }
}