package com.back.domain.post.comment.dto

import com.back.domain.post.comment.entity.Comment
import java.time.LocalDateTime

data class CommentDto(
    val id: Int,
    val content: String,
    val authorId: Int,
    val authorName: String,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime
) {
    constructor(comment: Comment) : this(
        comment.id,
        comment.content,
        comment.author.id,
        comment.author.name,
        comment.createDate,
        comment.modifyDate
    )
}
