package com.back.domain.member.controller

import com.back.domain.member.dto.MemberWithUsernameDto
import com.back.domain.member.service.MemberService
import com.back.standard.dto.PageDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/adm/members")
class ApiV1AdmMemberController(
    private val memberService: MemberService
) {

    @GetMapping
    fun list(
        @RequestParam("page", defaultValue = "1") page: Int,
        @RequestParam("pageSize", defaultValue = "2") pageSize: Int,
    ): PageDto<MemberWithUsernameDto> {

        val page = if(page >= 1) page else 1
        val pageSize = if(pageSize >= 5) pageSize else 5

        val pageResult = memberService.findByPaged(page, pageSize)

        return PageDto(
            pageResult.map {
                MemberWithUsernameDto(it)
            }
        )
    }
}
