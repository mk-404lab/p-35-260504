package com.back.domain.post.post.entity

import com.back.global.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity


@Entity
class PostBody(
    @Column(columnDefinition = "LONGTEXT")
    val content: String
) : BaseEntity(0){

}