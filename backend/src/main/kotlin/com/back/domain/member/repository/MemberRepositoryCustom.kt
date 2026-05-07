package com.back.domain.member.repository

import com.back.domain.member.entity.Member

interface MemberRepositoryCustom {

    fun findQById(id: Int): Member?
    fun findQByUsername(username: String): Member?
    fun findQByIdIn(ids: List<Int>): List<Member>
    fun findQByUsernameAndNickname(username: String, nickname: String): Member?
    fun findQByUsernameOrNickname(username: String, nickname: String): List<Member>

    fun findQByUsernameAndEitherPasswordOrNickname(
        username: String,
        password: String,
        nickname: String
    ): List<Member>

    fun findQByNicknameContaining(nickname: String): List<Member>
}