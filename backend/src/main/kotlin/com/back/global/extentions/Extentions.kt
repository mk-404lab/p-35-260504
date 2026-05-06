package com.back.global.extentions

fun <T : Any> T?.getOrThrow(): T {
    return this ?: throw NoSuchElementException()
}
