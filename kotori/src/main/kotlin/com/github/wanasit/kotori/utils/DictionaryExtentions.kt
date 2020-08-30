package com.github.wanasit.kotori.utils

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.TermDictionary
import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.optimized.PlainTermEntry
import com.github.wanasit.kotori.optimized.PlainToken

val <F> Dictionary<F>.termEntries: List<TermEntry<F>>
    get() = this.terms.map { it.second }

val <F> Dictionary<F>.size: Int
    get() = this.terms.size()

val <F> TermDictionary<F>.asEntries : List<TermEntry<F>>
    get() = this.map { it.second }

fun TermEntry<*>.withoutFeatures(): PlainTermEntry<PlainToken.EmptyFeatures> {
    return PlainTermEntry(this, PlainToken.EMPTY_FEATURES)
}