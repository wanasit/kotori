package com.github.wanasit.kotori.optimized


import org.junit.Test
import kotlin.test.assertEquals


class TestIndexedIntArray {

    fun IndexedIntArray.getMemberAtIndex(index: Int): List<Int> {
        val members: MutableList<Int> = mutableListOf()
        this.accessMembersAtIndex(index) { members.add(it) }
        return members
    }

    @Test
    fun testBasic() {

        val index = IndexedIntArray(4)

        assertEquals(false, index.hasMemberAtIndex(0))
        assertEquals(false, index.hasMemberAtIndex(1))
        assertEquals(false, index.hasMemberAtIndex(2))
        assertEquals(false, index.hasMemberAtIndex(3))

        index.insert(1, 1)
        index.insert(2, 2)

        assertEquals(false, index.hasMemberAtIndex(0))
        assertEquals(true, index.hasMemberAtIndex(1))
        assertEquals(true, index.hasMemberAtIndex(2))
        assertEquals(false, index.hasMemberAtIndex(3))

        assertEquals(listOf(1), index.getMemberAtIndex(1))
        assertEquals(listOf(2), index.getMemberAtIndex(2))

        for (i in 0..1000) {
            index.insert(0, i)
        }
        
        assertEquals(IntRange(0, 1000).toList(), index.getMemberAtIndex(0))
    }

}