/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.github.wanasit.kotori.mecab

import java.io.File
import java.util.zip.GZIPOutputStream
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestDictionary {

    @Test fun testReadFromResource() {
        val dictionary = MeCabDictionary.readFromResource()

        assertNotNull(dictionary.terms)
        assertNotNull(dictionary.connection)
        assertNotNull(dictionary.unknownExtraction)
        assertTrue(dictionary.terms.toList().size > 100000)
    }
}