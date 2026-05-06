package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Int>, MemberRepositoryCustom {

    fun findByUsername(username: String): Member?
    fun findByApiKey(apiKey: String): Member?
    fun findByIdIn(ids: List<Int>): List<Member>
    fun findByUsernameAndNickname(username: String, nickname: String): Member?
}
