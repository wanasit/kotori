package com.github.wanasit.kotori.optimized

import com.github.wanasit.kotori.TermEntry

data class PlainTermEntry<Features>(
        override val surfaceForm: String,
        override val leftId: Int,
        override val rightId: Int,
        override val cost: Int,
        override val features: Features
) : TermEntry<Features> {
    constructor(other: TermEntry<*>, features: Features) :
            this(other.surfaceForm, other.leftId, other.rightId, other.cost, features)
}