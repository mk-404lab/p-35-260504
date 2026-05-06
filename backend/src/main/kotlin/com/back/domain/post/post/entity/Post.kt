package com.back.domain.post.post.entity

import com.back.domain.member.entity.Member
import com.back.domain.post.comment.entity.Comment
import com.back.global.entity.BaseEntity
import com.back.global.exception.ServiceException
import jakarta.persistence.*
import java.util.*

@Entity
class Post(
    @ManyToOne(fetch = FetchType.LAZY) var author: Member,
    var title: String,
    var content: String,
    @OneToMany(
        mappedBy = "post",
        cascade = [CascadeType.PERSIST, CascadeType.REMOVE],
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    val comments: MutableList<Comment> = ArrayList<Comment>()
    ) :
    BaseEntity(0) {

    constructor(author: Member, title: String, content: String) : this(author, title, content, ArrayList<Comment>())

    fun update(title: String, content: String) {

        this.title = title
        this.content = content
    }

    // 댓글 추가
    fun addComment(author: Member, content: String): Comment {
        val comment = Comment(author, content, this)
        comments.add(comment)

        return comment
    }

    // 댓글 조회
    fun findCommentById(commentId: Int): Optional<Comment?> {
        return comments.stream()
            .filter { c: Comment? -> c!!.id == commentId }
            .findFirst()
    }

    // 댓글 삭제
    fun deleteComment(id: Int) {
        val comment = findCommentById(id).get()
        comments.remove(comment)
    }

    fun modifyComment(commentId: Int, content: String) {
        val comment = findCommentById(commentId).get()
        comment.update(content)
    }

    fun checkModify(actor: Member) {
        if (actor != this.author) {
            throw ServiceException("403-1", "수정 권한이 없습니다.")
        }
    }

    fun checkDelete(actor: Member) {
        if (actor != this.author) {
            throw ServiceException("403-2", "삭제 권한이 없습니다.")
        }
    }
}
