package com.github.wanasit.kotori.optimized
import com.github.wanasit.kotori.optimized.DefaultTermFeatures.*
import com.github.wanasit.kotori.FakingTermDictionary
import com.github.wanasit.kotori.fakeTermDictionary
import org.junit.Assert.*
import org.junit.Test



class TestDefaultTermFeatures {

    fun FakingTermDictionary<DefaultTermFeatures>.term(
            surfaceForm: String, leftId: Int, rightId: Int, cost: Int, partOfSpeech: DefaultTermFeatures.PartOfSpeech
    ) {
        this.term(surfaceForm, leftId, rightId, cost, features = DefaultTermFeatures(
                partOfSpeech = partOfSpeech
        ))
    }
}