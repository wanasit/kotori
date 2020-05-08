package com.github.wanasit.kotori.utils

import org.jetbrains.annotations.Contract

@Contract("null, _ -> fail")
fun <T> checkNonnull(obj: T?, msg: String = "Invalid argument"): T {
    return obj ?: throw IllegalArgumentException(msg)
}

@Contract("false, _ -> fail")
fun checkArgument(condition: Boolean, msg: String = "Invalid argument") {
    if (!condition) {
        throw IllegalArgumentException(msg)
    }
}