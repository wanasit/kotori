package com.github.wanasit.kotori.utils

private const val QUOTE = '"'
private const val COMMA = ','
private const val QUOTE_ESCAPED = "\"\""

object CSVUtil {

    fun writeLine(vararg input: Any): String {
        return input.joinToString(
                separator = COMMA.toString(),
                transform = { escape(it.toString()) })
    }

    fun parseLine(line: String): List<String> {
        var insideQuote = false
        val result: MutableList<String> = mutableListOf()
        var builder = StringBuilder()
        var quoteCount = 0
        for (c in line) {
            if (c == QUOTE) {
                insideQuote = !insideQuote
                quoteCount++
            }
            if (c == COMMA && !insideQuote) {
                var value = builder.toString()
                value = unescape(value)
                result.add(value)
                builder = StringBuilder()
                continue
            }
            builder.append(c)
        }
        result.add(builder.toString())
        if (quoteCount % 2 != 0) {
            throw RuntimeException("Unmatched quote in entry: $line")
        }
        return result
    }

    /**
     * Unescape input for CSV
     */
    private fun unescape(text: String): String {
        val builder = StringBuilder()
        var foundQuote = false
        for (i in 0 until text.length) {
            val c = text[i]
            if (i == 0 && c == QUOTE || i == text.length - 1 && c == QUOTE) {
                continue
            }
            if (c == QUOTE) {
                foundQuote = if (foundQuote) {
                    builder.append(QUOTE)
                    false
                } else {
                    true
                }
            } else {
                foundQuote = false
                builder.append(c)
            }
        }
        return builder.toString()
    }

    private fun escape(text: String): String {
        val hasQuote = text.indexOf(QUOTE) >= 0
        val hasComma = text.indexOf(COMMA) >= 0
        if (!(hasQuote || hasComma)) {
            return text
        }
        val builder = StringBuilder()
        if (hasQuote) {
            for (i in 0 until text.length) {
                val c = text[i]
                if (c == QUOTE) {
                    builder.append(QUOTE_ESCAPED)
                } else {
                    builder.append(c)
                }
            }
        } else {
            builder.append(text)
        }
        if (hasComma) {
            builder.insert(0, QUOTE)
            builder.append(QUOTE)
        }
        return builder.toString()
    }
}