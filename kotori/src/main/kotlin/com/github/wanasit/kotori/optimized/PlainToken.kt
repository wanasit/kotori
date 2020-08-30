package com.github.wanasit.kotori.optimized
import com.github.wanasit.kotori.Token

data class PlainToken<TermFeatures>(
        override val text: String,
        override val index: Int,
        override val features: TermFeatures) : Token<TermFeatures> {

    companion object {

        val EMPTY_FEATURES = EmptyFeatures();

        fun createWithEmptyFeatures(text: String, index: Int) : PlainToken<EmptyFeatures> {
            return PlainToken(text, index, EMPTY_FEATURES)
        }
    }

    override fun toString(): String {
        return text
    }
    
    fun List<Token<*>>.withoutFeatures() : List<Token<EmptyFeatures>> {
        return this.map { createWithEmptyFeatures(it.text, it.index) }
    }

    class EmptyFeatures {

        override fun equals(other: Any?): Boolean {
            return other is EmptyFeatures
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}
