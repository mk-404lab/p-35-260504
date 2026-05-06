package com.back.domain.member.service

import com.back.domain.member.entity.Member
import com.back.domain.member.repository.MemberRepository
import com.back.global.extentions.getOrThrow
import com.back.standard.ut.Ut
import com.back.standard.ut.Ut.jwt.isValid
import com.back.standard.ut.Ut.jwt.payloadOrNull
import com.back.standard.ut.Ut.jwt.toString
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.Map

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthTokenServiceTest {
    @Autowired
    private val authTokenService: AuthTokenService? = null

    @Autowired
    private val memberRepository: MemberRepository? = null

    private val secretPattern = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890"
    private val expireSeconds = 1000L * 60 * 60 * 24 * 365 // 1년

    @Test
    fun t1() {
        Assertions.assertThat<AuthTokenService?>(authTokenService).isNotNull()
    }

    @Test
    @DisplayName("jjwt 최신 방식으로 JWT 생성, {name=\"Paul\", age=23}")
    @Throws(InterruptedException::class)
    fun t2() {
        val payload = Map.of<String?, Any?>("name", "Paul", "age", 23)

        val jwt = Ut.jwt.toString(secretPattern, expireSeconds, payload)
        val parsedPayload = payloadOrNull(jwt, secretPattern)

        Assertions.assertThat(parsedPayload)
            .containsAllEntriesOf(payload)

        Assertions.assertThat(jwt).isNotBlank()


        println("jwt = " + jwt)
    }

    @Test
    @DisplayName("Ut.jwt.toString 를 통해서 JWT 생성, {name=\"Paul\", age=23}")
    fun t3() {
        val jwt = toString(
            secretPattern,
            expireSeconds,
            Map.of("name", "Paul", "age", 23)
        )

        Assertions.assertThat(jwt).isNotBlank()

        val rst = isValid(jwt, secretPattern)
        Assertions.assertThat(rst).isTrue()

        println("jwt = " + jwt)
    }

    @Test
    @DisplayName("AuthTokenService를 통해서 accessToken 생성")
    fun t4() {
        val member1: Member = memberRepository!!.findByUsername("user1").getOrThrow()
        val accessToken = authTokenService!!.genAccessToken(member1)
        Assertions.assertThat(accessToken).isNotBlank()

        val payload = authTokenService.payloadOrNull(accessToken)

        Assertions.assertThat<String?, Any?>(payload).containsAllEntriesOf(
            Map.of(
                "id", member1.id,
                "username", member1.username
            )
        )

        println("accessToken = " + accessToken)
    }
}
