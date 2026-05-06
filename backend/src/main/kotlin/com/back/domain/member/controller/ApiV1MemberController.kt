package com.back.domain.member.controller

import com.back.domain.member.dto.MemberDto
import com.back.domain.member.entity.Member
import com.back.domain.member.service.MemberService
import com.back.global.exception.ServiceException
import com.back.global.extentions.getOrThrow
import com.back.global.rq.Rq
import com.back.global.rsData.RsData
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/members")
class ApiV1MemberController(
    private val memberService: MemberService,
    private val rq: Rq
) {

    data class MemberJoinReqBody(
        @field:NotBlank @field:Size(min = 2, max = 30) val username: String,
        @field:NotBlank @field:Size(min = 2, max = 30) val password: String,
        @field:NotBlank @field:Size(min = 2, max = 30) val nickname: String
    )

    data class MemberJoinResBody(
        val memberDto: MemberDto
    )

    @PostMapping("/join")
    fun join(@RequestBody @Valid reqBody: @Valid MemberJoinReqBody): RsData<MemberJoinResBody> {
        val member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname)

        return RsData(
            "회원가입이 완료되었습니다. ${member.name}님 환영합니다.",
            "201-1",
            MemberJoinResBody(
                MemberDto(member)
            )
        )
    }

    data class MemberLoginReqBody(
        @field:NotBlank @field:Size(min = 2, max = 30) val username: String,
        @field:NotBlank @field:Size(min = 2, max = 30) val password: String
    )

    data class MemberLoginResBody(
        val apiKey: String,
        val accessToken: String
    )

    @PostMapping("/login")
    fun login(@RequestBody @Valid reqBody: @Valid MemberLoginReqBody): RsData<MemberLoginResBody?> {
        val actor: Member = memberService.findByUsername(reqBody.username)
            ?: throw ServiceException(
                "404-1",
                "존재하지 않는 회원입니다."
            )

        memberService.checkPassword(reqBody.password, actor.password)

        rq.addCookie("apiKey", actor.apiKey)
        val accessToken = memberService.genAccessToken(actor)
        rq.addCookie("accessToken", accessToken)

        return RsData(
            "${actor.name}님 환영합니다.",
            "200-1",
            MemberLoginResBody(
                actor.apiKey,
                accessToken
            )
        )
    }

    @DeleteMapping("/logout")
    fun logout(): RsData<Void?> {
        rq.deleteCookie("apiKey")
        rq.deleteCookie("accessToken")

        return RsData(
            "로그아웃 되었습니다.",
            "200-1"
        )
    }

    @GetMapping("/me")
    fun me(): MemberDto {
        // 인증 작업이 없다

        val tmpActor = rq.actor // user1 정보

        // 내 전체 회원 정보 조회가 목적
        val realActor = memberService.findById(tmpActor.id).getOrThrow()

        return MemberDto(realActor)
    }
}
