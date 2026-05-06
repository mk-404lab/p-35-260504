package com.back.domain.post.comment.entity

import com.back.domain.member.entity.Member
import com.back.domain.post.post.entity.Post
import com.back.global.entity.BaseEntity
import com.back.global.exception.ServiceException
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

@Entity
class Comment(
    @ManyToOne var author: Member,
    var content: String,
    @ManyToOne var post: Post
) : BaseEntity(0) {

    fun update(content: String) {
        this.content = content
    }

    fun checkActorModify(actor: Member) {
        if (this.author != actor) {
            throw ServiceException("403-1", "댓글 수정 권한이 없습니다.")
        }
    }

    fun checkActorDelete(actor: Member) {
        if (this.author != actor) {
            throw ServiceException("403-2", "댓글 삭제 권한이 없습니다.")
        }
    }
}
