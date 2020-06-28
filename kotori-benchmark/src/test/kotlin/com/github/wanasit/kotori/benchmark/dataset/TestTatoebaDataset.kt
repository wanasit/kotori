package com.github.wanasit.kotori.benchmark.dataset

import org.junit.Test
import kotlin.test.assertTrue

class TestTatoebaDataset {

    @Test fun loadDataSet() {
        val dataset = TatoebaDataset.loadJapaneseSentences()
        assertTrue(dataset.isNotEmpty())
    }
}