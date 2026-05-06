package com.back.domain.member.repository

import com.back.global.extentions.getOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun `findById()`() {
        // 이 테스트는 memberRepository에서 네이밍 메서드를 사용하는 기존의 jpa 방식을 검증하는 과정이다.
        // 당연히 문제 없이 통과하는 케이스다.
        // 앞으로 QueryDSL을 사용해서 jpa 방식의 네이밍 메서드를 똑같이 동작하도록 만들고, 이를 테스트 할 것

        val member = memberRepository.findById(1).get()
        assertThat(member.id).isEqualTo(1)
    }

    @Test
    fun `findQById()`() {
        val member = memberRepository.findQById(1).getOrThrow()
        assertThat(member.id).isEqualTo(1)
    }

    @Test
    fun `findByUsername()`() {
        val member = memberRepository.findByUsername("user1").getOrThrow()
        assertThat(member.username).isEqualTo("user1")
    }

    @Test
    fun `findQByUsername()`() {
        val member = memberRepository.findQByUsername("user1").getOrThrow()
        assertThat(member.username).isEqualTo("user1")
    }

    @Test
    fun `findByIdIn()`() {
        val memberList = memberRepository.findByIdIn(listOf(1, 2, 3))
        assertThat(memberList.map{ it.id}).containsAnyOf(1, 2, 3)
    }

    @Test
    fun `findQByIdIn()`() {
        val memberList = memberRepository.findQByIdIn(listOf(1, 2, 3))
        assertThat(memberList.map{ it.id}).containsAnyOf(1, 2, 3)
    }

    @Test
    fun `findByUsernameAndUserNickname()`() {
        val member = memberRepository.findByUsernameAndNickname("user1", "유저1").getOrThrow()

        assertThat(member.username).isEqualTo("user1")
        assertThat(member.nickname).isEqualTo("유저1")
    }

    @Test
    fun `findQByUsernameAndUserNickname()`() {
        val member = memberRepository.findQByUsernameAndNickname("user1", "유저1").getOrThrow()

        assertThat(member.username).isEqualTo("user1")
        assertThat(member.nickname).isEqualTo("유저1")
    }

}