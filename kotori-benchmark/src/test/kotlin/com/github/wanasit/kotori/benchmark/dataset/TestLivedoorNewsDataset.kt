package com.github.wanasit.kotori.benchmark.dataset

import org.junit.Test

class TestLivedoorNewsDataset {

    @Test fun loadDataSet() {
        val dataset = LivedoorNewsDataset.loadDataset()
        kotlin.test.assertTrue(dataset.isNotEmpty())
    }
}