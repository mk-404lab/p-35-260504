package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import com.back.domain.member.entity.QMember
import com.querydsl.jpa.impl.JPAQueryFactory

class MemberRepositoryImpl (
    private val jpaQueryFactory: JPAQueryFactory
) : MemberRepositoryCustom {

    override fun findQById(id: Int): Member? {

        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.id.eq(id))    // where member.id = id
            .fetchOne()     //limit 1
    }

    override fun findQByUsername(username: String): Member? {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetchOne() // limit 1
    }
}