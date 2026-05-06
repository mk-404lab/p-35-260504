package com.back.standard.ut

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*

object Ut {
    object jwt {
        fun toString(secret: String, expireSeconds: Long, body: Map<String, Any>): String {
            val claimsBuilder = Jwts.claims()

            for (entry in body.entries) {
                claimsBuilder.add(entry.key, entry.value)
            }

            val claims = claimsBuilder.build()

            val issuedAt = Date()
            val expiration = Date(issuedAt.getTime() + 1000L * expireSeconds)

            val secretKey: Key = Keys.hmacShaKeyFor(secret.toByteArray())

            val jwt = Jwts.builder()
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact()

            return jwt
        }

        fun isValid(jwt: String?, secret: String): Boolean {
            val keyBytes = secret.toByteArray(StandardCharsets.UTF_8)
            val secretKey = Keys.hmacShaKeyFor(keyBytes)

            try {
                Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parse(jwt)
                    .getPayload()

                return true
            } catch (e: Exception) {
                return false
            }
        }

        fun payloadOrNull(jwt: String, secret: String): Map<String, Any>? {
            val keyBytes = secret.toByteArray(StandardCharsets.UTF_8)
            val secretKey = Keys.hmacShaKeyFor(keyBytes)

            if (isValid(jwt, secret)) {
                return Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parse(jwt)
                    .getPayload() as Map<String, Any>
            }

            return null
        }
    }
}
