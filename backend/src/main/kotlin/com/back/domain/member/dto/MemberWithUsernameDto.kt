package com.back.domain.member.dto

import com.back.domain.member.entity.Member
import java.time.LocalDateTime

data class MemberWithUsernameDto(
    val id: Int,
    val name: String,
    val username: String,
    val nickname: String,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime
) {
    constructor(member: Member) : this(
        member.id,
        member.name,
        member.username,
        member.nickname,
        member.createDate,
        member.createDate
    )
}
