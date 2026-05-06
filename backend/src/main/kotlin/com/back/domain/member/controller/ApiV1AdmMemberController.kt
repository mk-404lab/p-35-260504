package com.back.domain.member.controller

import com.back.domain.member.dto.MemberWithUsernameDto
import com.back.domain.member.service.MemberService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/adm/members")
class ApiV1AdmMemberController(
    private val memberService: MemberService
) {

    @GetMapping
    fun list(): List<MemberWithUsernameDto> {
        return memberService.findAll()
            .map { MemberWithUsernameDto(it) }
    }
}
