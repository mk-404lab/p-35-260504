package com.back.domain.member.service

import com.back.domain.member.entity.Member
import com.back.standard.ut.Ut
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
internal class AuthTokenService(
    @Value("\${custom.jwt.secretPattern}")
    private val secretKey: String,

    @Value("\${custom.jwt.expiration}")
    private val expireTime: Long
) {
    fun genAccessToken(member: Member): String {
        return Ut.jwt.toString(
            secretKey,
            expireTime,
            mapOf(
                "id" to member.id,
                "username" to member.username,
                "nickname" to member.nickname
            )
        )
    }

    fun payloadOrNull(jwt: String): Map<String, Any>? {
        return Ut.jwt.payloadOrNull(jwt, secretKey)
    }
}
