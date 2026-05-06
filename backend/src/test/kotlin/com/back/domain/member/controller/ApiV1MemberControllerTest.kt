package com.back.domain.member.controller

import com.back.domain.member.entity.Member
import com.back.domain.member.repository.MemberRepository
import com.back.global.extentions.getOrThrow
import com.back.standard.ut.Ut
import jakarta.servlet.http.Cookie
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ApiV1MemberControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc
    @Autowired
    private lateinit var memberRepository: MemberRepository
    @Value("\${custom.jwt.secretPattern}")
    private lateinit var secretPattern: String

    @Test
    @DisplayName("회원 가입")
    @Throws(Exception::class)
    fun t1() {
        val username = "newUser"
        val password = "1234"
        val nickname = "새유저"

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/members/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "username": "${username}",
                                            "password": "${password}",
                                            "nickname": "${nickname}"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1MemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("join"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("201-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("회원가입이 완료되었습니다. ${nickname}님 환영합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberDto.id").value(6))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberDto.createDate").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberDto.modifyDate").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberDto.name").value(nickname))
    }

    @Test
    @DisplayName("회원 가입, 이미 존재하는 username으로 가입 - user1로 가입")
    @Throws(Exception::class)
    fun t2() {
        val username = "user1"
        val password = "1234"
        val nickname = "새유저"

        val resultActions = mvc!!
            .perform(
                MockMvcRequestBuilders.post("/api/v1/members/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "username": "${username}",
                                            "password": "${password}",
                                            "nickname": "${nickname}"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1MemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("join"))
            .andExpect(MockMvcResultMatchers.status().isConflict())
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("409-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("이미 사용중인 아이디입니다."))
    }

    @Test
    @DisplayName("로그인")
    @Throws(Exception::class)
    fun t3() {
        val username = "user1"
        val password = "1234"
        val apiKey = "user1"

        val resultActions = mvc!!
            .perform(
                MockMvcRequestBuilders.post("/api/v1/members/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "username": "${username}",
                                            "password": "${password}"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        val member: Member = memberRepository!!.findByUsername(username).getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1MemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${member.nickname}님 환영합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.apiKey").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").isNotEmpty())

        resultActions.andExpect(
            ResultMatcher { result: MvcResult? ->
                val apiKeyCookie = result!!.getResponse().getCookie("apiKey")
                Assertions.assertThat<Cookie?>(apiKeyCookie).isNotNull()
                Assertions.assertThat(apiKeyCookie!!.getValue()).isEqualTo(apiKey)

                Assertions.assertThat(apiKeyCookie.getPath()).isEqualTo("/")
                Assertions.assertThat(apiKeyCookie.isHttpOnly()).isTrue()
                Assertions.assertThat(apiKeyCookie.getDomain()).isEqualTo("localhost")


                val accessTokenCookie = result.getResponse().getCookie("accessToken")
                Assertions.assertThat<Cookie?>(accessTokenCookie).isNotNull()

                Assertions.assertThat(accessTokenCookie!!.getPath()).isEqualTo("/")
                Assertions.assertThat(accessTokenCookie.getDomain()).isEqualTo("localhost")
                Assertions.assertThat(accessTokenCookie.isHttpOnly()).isEqualTo(true)
            }
        )
    }

    @Test
    @DisplayName("로그아웃")
    @Throws(Exception::class)
    fun t4() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.delete("/api/v1/members/logout")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1MemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("logout"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("로그아웃 되었습니다."))
            .andExpect(ResultMatcher { result: MvcResult? ->
                val apiKeyCookie = result!!.getResponse().getCookie("apiKey")
                Assertions.assertThat(apiKeyCookie!!.getValue()).isEmpty()
                Assertions.assertThat(apiKeyCookie.getMaxAge()).isEqualTo(0)
                Assertions.assertThat(apiKeyCookie.getPath()).isEqualTo("/")
                Assertions.assertThat(apiKeyCookie.isHttpOnly()).isTrue()
            })
    }

    @Test
    @DisplayName("내 정보")
    @Throws(Exception::class)
    fun t5() {
        val actor: Member = memberRepository!!.findByUsername("user1").getOrThrow()
        val actorApiKey = actor.apiKey

        val resultActions = mvc!!
            .perform(
                MockMvcRequestBuilders.get("/api/v1/members/me")
                    .header("Authorization", "Bearer " + actorApiKey)
            )
            .andDo(MockMvcResultHandlers.print())

        val member: Member = memberRepository.findByUsername("user1").getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1MemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("me"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(member.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.createDate").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.modifyDate").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(member.name))
    }

    @Test
    @DisplayName("내 정보, 올바른 API KEY, 유효하지 않은 accessToken")
    @Throws(Exception::class)
    fun t6() {
        val actor: Member = memberRepository.findByUsername("user1").getOrThrow()
        val actorApiKey = actor.apiKey

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/members/me")
                    .cookie(Cookie("apiKey", actorApiKey), Cookie("accessToken", "wrong-access-token"))
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1MemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("me"))
            .andExpect(MockMvcResultMatchers.status().isOk())

        resultActions
            .andExpect(ResultMatcher { result: MvcResult ->
                val accessTokenCookie = result!!.getResponse().getCookie("accessToken")
                Assertions.assertThat<Cookie?>(accessTokenCookie).isNotNull()

                Assertions.assertThat(accessTokenCookie!!.getPath()).isEqualTo("/")
                Assertions.assertThat(accessTokenCookie.getDomain()).isEqualTo("localhost")
                Assertions.assertThat(accessTokenCookie.isHttpOnly()).isEqualTo(true)

                val newAccessToken = accessTokenCookie.getValue()
                Assertions.assertThat(newAccessToken).isNotEqualTo("wrong-access-token")
                Assertions.assertThat(Ut.jwt.isValid(newAccessToken, secretPattern!!)).isTrue()
            })
    }
}