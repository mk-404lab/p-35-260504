package com.back.domain.post.post.dto

import com.back.domain.post.post.entity.Post
import java.time.LocalDateTime

data class PostDto(
    val id: Int,
    val title: String,
    val content: String,
    val authorId: Int,
    val authorName: String,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime
) {
    constructor(post: Post) : this(
        post.id,
        post.title,
        post.content,
        post.author.id,
        post.author.name,
        post.createDate,
        post.modifyDate
    )
}
