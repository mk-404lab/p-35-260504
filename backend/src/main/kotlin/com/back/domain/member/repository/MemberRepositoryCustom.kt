package com.back.domain.member.repository

import com.back.domain.member.entity.Member

interface MemberRepositoryCustom {

    fun findQById(id: Int): Member?
}