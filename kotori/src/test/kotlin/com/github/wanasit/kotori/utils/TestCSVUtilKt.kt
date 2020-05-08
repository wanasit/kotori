package com.github.wanasit.kotori.utils

import kotlin.test.Test
import kotlin.test.assertEquals


class TestCSVUtilKt {

    @Test fun testParseLine() {

        assertEquals(
                listOf("DEFAULT", "5", "5", "4769", "記号","一般","*","*","*","*","*"),
                CSVUtil.parseLine("DEFAULT,5,5,4769,記号,一般,*,*,*,*,*")
        )
    }

    @Test fun testWriteLine() {

        assertEquals(
                "DEFAULT,5,5,4769,記号,一般,*,*,*,*,*",
                CSVUtil.writeLine("DEFAULT", "5", "5", "4769", "記号","一般","*","*","*","*","*")
        )

        assertEquals(
                "\"ab,cd\",5,5",
                CSVUtil.writeLine("ab,cd", "5", "5")
        )

        assertEquals(
                "\"ab,cd\"\"inner\"\"\",5,5",
                CSVUtil.writeLine("ab,cd\"inner\"", "5", "5")
        )
    }
}