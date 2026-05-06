package com.back.global.security

import com.back.domain.member.entity.Member
import com.back.domain.member.service.MemberService
import com.back.global.exception.ServiceException
import com.back.global.rq.Rq
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CustomAuthenticationFilter(
    private val memberService: MemberService,
    private val rq: Rq
) : OncePerRequestFilter() {


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.debug("CustomAuthenticationFilter called")

        try {
            authenticate(request, response, filterChain)
        } catch (e: ServiceException) {
            val rsData = e.rsData
            response.contentType = "application/json"
            response.status = rsData.statusCode
            response.writer.write(
                """
                    {
                        "resultCode": "${rsData.resultCode}",
                        "msg": "${rsData.msg}"
                    }
                    
                    """.trimIndent()
            )
        } catch (e: Exception) {
            throw e
        }
    }

    private fun authenticate(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        if (!request.requestURI.startsWith("/api/")) {
            filterChain.doFilter(request, response)
            return
        }

        if (listOf("/api/v1/members/join", "/api/v1/members/login").contains(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        val apiKey: String
        val accessToken: String

        val headerAuthorization = rq.getHeader("Authorization", "")

        if (!headerAuthorization.isBlank()) {
            if (!headerAuthorization.startsWith("Bearer ")) throw ServiceException(
                "401-2",
                "Authorization 헤더가 Bearer 형식이 아닙니다."
            )

            val headerAuthorizationBits = headerAuthorization.split(" ".toRegex(), limit = 3).toTypedArray()

            apiKey = headerAuthorizationBits[1]
            accessToken = if (headerAuthorizationBits.size == 3) headerAuthorizationBits[2] else ""
        } else {
            apiKey = rq.getCookieValue("apiKey", "")
            accessToken = rq.getCookieValue("accessToken", "")
        }

        val isAdiKeyExists = !apiKey.isBlank()
        val isAccessTokenExists = !accessToken.isBlank()

        if (!isAdiKeyExists && !isAccessTokenExists) {
            filterChain.doFilter(request, response)
            return
        }

        var member: Member? = null
        var isAccessTokenValid = false

        if (isAccessTokenExists) {
            val payload = memberService.payloadOrNull(accessToken)

            if (payload != null) {
                val id = payload["id"] as Int
                val username = payload["username"] as String
                val nickname = payload["nickname"] as String

                member = Member(id, username, nickname)
                isAccessTokenValid = true
            }
        }

        if (member == null) {
            member = memberService
                .findByApiKey(apiKey)
                ?: throw ServiceException(
                    "401-3",
                    "API 키가 유효하지 않습니다."
                )

        }

        if (isAccessTokenExists && !isAccessTokenValid) {
            val newAccessToken = memberService.genAccessToken(member!!)
            rq.addCookie("accessToken", newAccessToken)
            rq.setHeader("accessToken", newAccessToken)
        }

        val user: UserDetails = SecurityUser(
            member!!.id,
            member.username,
            "",
            member.nickname,
            member.authorities
        )

        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            user,
            user.password,
            user.authorities
        )


        SecurityContextHolder
            .getContext().authentication = authentication


        filterChain.doFilter(request, response)
    }
}