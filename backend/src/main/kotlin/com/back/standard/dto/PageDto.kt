package com.back.standard.dto

import org.springframework.data.domain.Page

data class PageDto<T : Any> private constructor(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val page: Int,
    val size: Int
) {
    constructor(page: Page<T>): this(
        content = page.content,
        totalElements = page.totalElements.toInt(),
        totalPages = page.totalPages,
        page = page.number,
        size = page.size
    )
}