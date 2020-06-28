package com.github.wanasit.kotori.utils

object CSVUtil {

    fun writeLine(vararg input: Any,
                  separator: Char = ',',
                  quote: Char = '"',
                  quoteEscape: String = "\"\""): String {
        return input.joinToString(
                separator = separator.toString(),
                transform = { escape(it.toString(), separator, quote, quoteEscape) })
    }

    fun parseLine(line: String,
                  separator: Char = ',',
                  quote: Char? = '"'
    ): List<String> {
        var insideQuote = false
        val result: MutableList<String> = mutableListOf()
        var builder = StringBuilder()
        var quoteCount = 0
        for (c in line) {
            if (c == quote) {
                insideQuote = !insideQuote
                quoteCount++
            }
            if (c == separator && !insideQuote) {
                var value = builder.toString()
                value = unescape(value, quote)
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
    private fun unescape(
            text: String,
            quote: Char?
    ): String {
        val builder = StringBuilder()
        var foundQuote = false
        for (i in 0 until text.length) {
            val c = text[i]
            if (i == 0 && c == quote || i == text.length - 1 && c == quote) {
                continue
            }
            if (c == quote) {
                foundQuote = if (foundQuote) {
                    builder.append(quote)
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

    private fun escape(
            text: String,
            separator: Char,
            quote: Char,
            quoteEscape: String
    ): String {
        val hasQuote = text.indexOf(quote) >= 0
        val hasSeparator = text.indexOf(separator) >= 0
        if (!(hasQuote || hasSeparator)) {
            return text
        }
        val builder = StringBuilder()
        if (hasQuote) {
            for (i in 0 until text.length) {
                val c = text[i]
                if (c == quote) {
                    builder.append(quoteEscape)
                } else {
                    builder.append(c)
                }
            }
        } else {
            builder.append(text)
        }
        if (hasSeparator) {
            builder.insert(0, quote)
            builder.append(quote)
        }
        return builder.toString()
    }
}