package com.back.domain.post.post.repository

import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.entity.QPost
import com.back.standard.enums.PostSearchKeywordType
import com.back.standard.enums.PostSearchSortType
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils


class PostRepositoryImpl(
    private val jpaQuery: JPAQueryFactory
) : PostRepositoryCustom {

    override fun findQPagedByKw(kwType: PostSearchKeywordType, kw: String, pageable: Pageable): Page<Post> {
        val post = QPost.post

        val builder = BooleanBuilder().apply {
            when (kwType) {
                PostSearchKeywordType.TITLE -> this.and(post.title.contains(kw))
                PostSearchKeywordType.CONTENT -> this.and(post.content.contains(kw))
                PostSearchKeywordType.AUTHOR_NICKNAME -> this.and(post.author.nickname.contains(kw))
                PostSearchKeywordType.ALL -> {
                    this.and(
                        post.title.contains(kw).or(
                            post.content.contains(kw)
                        )
                    )
                }
            }
        }

        val query = jpaQuery
            .selectFrom(post)
            .join(post.author).fetchJoin()
            .where(builder)

        pageable.sort.forEach { order ->
            val path = when (order.property.lowercase()) {
                PostSearchSortType.ID.property -> post.id
                else -> null
            }

            path?.let { property ->
                OrderSpecifier(
                    if (order.isAscending) Order.ASC else Order.DESC,
                    property
                )?.also {
                    query.orderBy(it)
                }
            }

        }

        val content = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageableExecutionUtils.getPage(content, pageable) {
            jpaQuery
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne() ?: 0L
        }
    }

}