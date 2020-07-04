package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.Token
import java.lang.UnsupportedOperationException

data class DefaultToken<TermFeatures>(
        override val text: String,
        override val index: Int) : Token<TermFeatures> {

    override fun toString(): String {
        return text
    }

    override val features: TermFeatures
        get() {
            throw UnsupportedOperationException()
        }
}
