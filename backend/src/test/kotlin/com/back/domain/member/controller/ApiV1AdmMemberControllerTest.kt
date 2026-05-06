package com.back.domain.member.controller

import com.back.domain.member.entity.Member
import com.back.domain.member.repository.MemberRepository
import com.back.global.extentions.getOrThrow
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ApiV1AdmMemberControllerTest {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    @DisplayName("회원 다건 조회")
    @Throws(Exception::class)
    fun t1() {
        val actor: Member = memberRepository.findByUsername("admin").getOrThrow()

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/adm/members")
                    .header(
                        "Authorization", "Bearer ${actor.apiKey}"
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1AdmMemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("list"))
            .andExpect(MockMvcResultMatchers.status().isOk())

        resultActions
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(5))
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$[*].id",
                    Matchers.containsInRelativeOrder(1, 5)
                )
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].createDate").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].modifyDate").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].nickname").value("시스템"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("system"))


        // 하나 또는 2개 정도만 검증


//        for(int i = 0; i < posts.size(); i++) {
//            Post post = posts.get(i);
//
//            resultActions
//                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(post.getId()))
//                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(matchesPattern(post.getCreateDate().toString().replaceAll("0+$", "") + ".*")))
//                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(matchesPattern(post.getModifyDate().toString().replaceAll("0+$", "") + ".*")))
//                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(post.getTitle()))
//                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(post.getContent()));
//        }
    }

    @Test
    @DisplayName("회원 다건 조회, 권한이 없는 경우")
    @Throws(Exception::class)
    fun t2() {
        val actor: Member = memberRepository.findByUsername("user1").getOrThrow()

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/adm/members")
                    .header(
                        "Authorization", "Bearer ${actor.apiKey}"
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("403-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("권한이 없습니다."))
    }
}
