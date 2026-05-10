package com.back.domain.post.post.repository

import com.back.domain.post.post.entity.Post
import com.back.standard.enums.PostSearchKeywordType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostRepositoryCustom {
    fun findQPagedByKw(kwType: PostSearchKeywordType, kw: String, pageable: Pageable): Page<Post>
}