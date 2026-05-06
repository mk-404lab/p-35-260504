package com.back.domain.member.dto

import com.back.domain.member.entity.Member
import java.time.LocalDateTime

data class MemberDto(
    val id: Int,
    val name: String,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime
) {
    constructor(member: Member) : this(
        member.id,
        member.name,
        member.createDate,
        member.modifyDate
    )
}
