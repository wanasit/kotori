package com.github.wanasit.kotori.ahocorasick

import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals

fun MutableDFA.put(text: String): State {
    return this.put(*text.map { it.toInt() }.toIntArray())
}

fun DFA.get(text: String): State {
    var state = DFA.ROOT
    for (i in text.chars()) {
        state = this.nextState(state, i)
        if (state == DFA.NONE) {
            return state
        }
    }

    return state
}


class TestDFA {




    @Test
    fun testBasicMatching() {
        val trie = TripleArrayTrie()
        assertEquals(trie.put("abc"), trie.get("abc"))
        assertEquals(trie.put("aaa"), trie.get("aaa"))
        assertEquals(trie.put("cda"), trie.get("cda"))


        val transition = trie.getTransition(trie.get("a"))
        assertEquals(setOf('a'.toInt(), 'b'.toInt()), transition.keys)
    }


}

