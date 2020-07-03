package com.github.wanasit.kotori.utils

import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.TermEntry

val <T: TermEntry> Dictionary<T>.termEntries: List<T>
    get() = this.terms.map { it.second }

val <T: TermEntry> Dictionary<T>.size: Int
    get() = this.terms.size()
