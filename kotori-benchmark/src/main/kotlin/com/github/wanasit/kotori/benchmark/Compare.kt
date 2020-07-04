package com.github.wanasit.kotori.benchmark

import com.github.wanasit.kotori.AnyToken
import com.github.wanasit.kotori.AnyTokenizer
import com.github.wanasit.kotori.Token
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.benchmark.dataset.LivedoorNewsDataset
import com.github.wanasit.kotori.benchmark.dataset.TextDatasetEntry
import kotlin.math.max
import kotlin.math.min

object Compare {

    fun <T: TextDatasetEntry> compareTokenizers (
            dataset: Collection<T>,
            baseTokenizer: AnyTokenizer,
            tokenizer: AnyTokenizer,
            diffReport: ((
                    datasetEntry: T,
                    baseResult: List<AnyToken>,
                    result: List<AnyToken>,
                    diffIndex: Int) -> Unit
            )?
    ) {
        for (doc in dataset) {
            val baseResult = baseTokenizer.tokenize(doc.text).filter { it.text.isNotBlank() }
            val result = tokenizer.tokenize(doc.text).filter { it.text.isNotBlank() }
            val diffToken = findDiffToken(baseResult, result)

            if (diffToken != null) {
                val diff = diffToken.first ?: diffToken.second!!
                diffReport?.invoke(doc, baseResult, result, diff)
            }
        }
    }

    fun findDiffToken(
            baseResult: List<AnyToken>,
            result: List<AnyToken>
    ) : Pair<Int?, Int?>? {

        for (i in baseResult.indices) {

            if (result.size <= i) {
                return i to null
            }

            if (baseResult[i].text != result[i].text ||
                    baseResult[i].index != result[i].index) {

                return i to i
            }
        }

        if (result.size > baseResult.size) {
            return null to baseResult.size
        }

        return null
    }
}



// ---------------------------------------------------------

fun main() {
    val dataset = LivedoorNewsDataset.loadDataset()

    val baseTokenizer = Tokenizers.loadKuromojiIpadicTokenizer()
    val tokenizer = Tokenizer.createDefaultTokenizer()

    Compare.compareTokenizers(dataset, baseTokenizer, tokenizer) { datasetEntry, baseResult, result, diffIndex ->

        val baseResultSub = baseResult.subList(max(0, diffIndex-5), min(baseResult.size, diffIndex + 5))
        val resultSub = result.subList(max(0, diffIndex-5), min(result.size, diffIndex + 5))

        val text = datasetEntry.text.substring(baseResultSub.first().index,
                baseResultSub.last().index + baseResultSub.last().text.length)

        println("==========================================================")
        println("On '${datasetEntry.title}'")
        println("\"${text.replace("\n", " ")}\"")
        println("expected > ${baseResultSub}")
        println("found    > ${resultSub}")
    }
}