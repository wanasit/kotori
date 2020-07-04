package com.github.wanasit.kotori.utils

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.TermEntry

val <F> Dictionary<F>.termEntries: List<TermEntry<F>>
    get() = this.terms.map { it.second }

val <F> Dictionary<F>.size: Int
    get() = this.terms.size()
