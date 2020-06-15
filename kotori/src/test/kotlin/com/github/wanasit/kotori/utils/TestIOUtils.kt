package com.github.wanasit.kotori.utils

import org.junit.Test
import org.junit.Assert.assertArrayEquals

class TestIOUtils {

    @Test
    fun testIntArrayReadWrite() {

        val file = createTempFile()

        val arrayA = intArrayOf(1, -1, 4589)
        val arrayB = intArrayOf(99, 123, -754)

        file.outputStream().use {
            IOUtils.writeIntArray(it, arrayA)
            IOUtils.writeIntArray(it, arrayB, includeSize = false)
        }

        file.inputStream().use {
            val readArrayA = IOUtils.readIntArray(it)
            assertArrayEquals(readArrayA, arrayA)

            val readArrayB = IOUtils.readIntArray(it, arrayB.size)
            assertArrayEquals(readArrayB, arrayB)
        }
    }
}