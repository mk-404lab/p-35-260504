package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


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
    fun countQByNicknameContaining(nickname: String): Long
    fun existsQByNicknameContaining(nickname: String): Boolean
    fun findQByNicknameContaining(nickname: String, pageable: Pageable): Page<Member>
    fun findQByNicknameContainingOrderByIdDesc(nickname: String): List<Member>

}