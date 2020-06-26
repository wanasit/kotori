package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.Token

data class SimpleToken(
        override val text: String,
        override val position: Int) : Token {

    override fun toString(): String {
        return text
    }
}