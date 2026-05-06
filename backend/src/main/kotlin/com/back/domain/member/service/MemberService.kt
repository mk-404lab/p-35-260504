package com.back.domain.member.service

import com.back.domain.member.entity.Member
import com.back.domain.member.repository.MemberRepository
import com.back.global.exception.ServiceException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Autowired
    private lateinit var authTokenService: AuthTokenService

    fun join(
        username: String,
        password: String,
        nickname: String,
        apiKey: String = UUID.randomUUID().toString()
    ): Member {
        findByUsername(username)?.let {
            throw ServiceException("409-1", "이미 사용중인 아이디입니다.")
        }

        val member = Member(username, passwordEncoder.encode(password)!!, nickname, apiKey)
        return memberRepository.save<Member>(member)
    }

    fun count(): Long =
        memberRepository.count()

    fun findByUsername(username: String): Member? =
        memberRepository.findByUsername(username)

    fun findByApiKey(apiKey: String): Member? =
        memberRepository.findByApiKey(apiKey)


    fun genAccessToken(member: Member): String =
        authTokenService.genAccessToken(member)


    fun payloadOrNull(jwt: String): Map<String, Any>? =
        authTokenService.payloadOrNull(jwt)

    fun findById(id: Int): Member? =
        memberRepository.findByIdOrNull(id)


    fun findAll(): MutableList<Member> =
        memberRepository.findAll()


    fun checkPassword(inputPassword: String, rawPassword: String) {
        if (!passwordEncoder.matches(inputPassword, rawPassword)) {
            throw ServiceException("401-2", "비밀번호가 일치하지 않습니다.")
        }
    }
}
