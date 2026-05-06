package com.back.domain.member.repository

import com.back.domain.member.entity.Member

class MemberRepositoryImpl : MemberRepositoryCustom {

    override fun findQById(id: Int): Member? {
        return Member(1, "test", "test")
    }
}