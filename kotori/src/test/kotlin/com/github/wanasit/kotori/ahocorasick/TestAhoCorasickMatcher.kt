package com.github.wanasit.kotori.ahocorasick

import org.junit.Assert
import org.junit.Test


class TestAhoCorasickMatcher {

    @Test
    fun testBasicMatching() {
        val words = listOf("a", "ab", "bab", "bc", "bca", "c", "caa")
        val matcher = AhoCorasickPatternMatcher(words.map { it to it })

        val outputs = matcher.findAllMatches("abccaab")

        Assert.assertEquals(listOf("a"), outputs[0])
        Assert.assertEquals(listOf("ab"), outputs[1])
        Assert.assertEquals(listOf("bc", "c"), outputs[2])
        Assert.assertEquals(listOf("c"), outputs[3])
        Assert.assertEquals(listOf("a"), outputs[4])
        Assert.assertEquals(listOf("a", "caa"), outputs[5])
        Assert.assertEquals(listOf("ab"), outputs[6])
    }


}

