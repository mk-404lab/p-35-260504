package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MemberRepository : JpaRepository<Member, Int>, MemberRepositoryCustom {

    fun findByUsername(username: String): Member?
    fun findByApiKey(apiKey: String): Member?
    fun findByIdIn(ids: List<Int>): List<Member>
    fun findByUsernameAndNickname(username: String, nickname: String): Member?

    @Query("SELECT m FROM Member m WHERE m.username = :username AND (m.password = :password OR m.nickname = :nickname)")
    fun findCByUsernameAndEitherPasswordOrNickname(username: String, password: String, nickname: String): List<Member>
}
