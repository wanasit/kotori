package com.github.wanasit.kotori.utils

fun Int.format(format: String="%,d") : String {
    return format.format(this)
}

fun Long.format(format: String="%,d") : String {
    return format.format(this)
}