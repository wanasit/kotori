package com.github.wanasit.kotori.optimized.tries

import org.junit.Test
import kotlin.test.assertEquals

class TestDFA {

    @Test
    fun testHashMapTrie() {
        val trie = HashMapTrie()
        assertEquals(trie.insert("abc"), trie.get("abc"))
        assertEquals(trie.insert("aaa"), trie.get("aaa"))
        assertEquals(trie.insert("cda"), trie.get("cda"))

        val transition = trie.getTransition(trie.get("a"))
        assertEquals(setOf('a'.toInt(), 'b'.toInt()), transition.keys)
    }


    @Test
    fun testHybridArrayTrie() {
        val base = HashMapTrie()
        base.insert("abc")
        base.insert("aaa")
        base.insert("cda")

        val trie = TransitionArrayTrie(base)
        assertEquals(base.get("abc"), trie.get("abc"))
        assertEquals(base.get("aaa"), trie.get("aaa"))
        assertEquals(base.get("cda"), trie.get("cda"))
        assertEquals(base.get("ab"), trie.get("ab"))
        assertEquals(base.get("ac"), trie.get("ac"))
        assertEquals(base.get("abcd"), trie.get("abcd"))

        assertEquals(DFA.NONE, trie.get("ac"))
        assertEquals(DFA.NONE, trie.get("abcd"))
    }

}

