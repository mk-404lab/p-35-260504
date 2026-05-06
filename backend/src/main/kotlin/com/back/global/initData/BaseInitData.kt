package com.back.global.initData

import com.back.domain.member.service.MemberService
import com.back.domain.post.post.service.PostService
import com.back.global.extentions.getOrThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.Transactional


@Configuration
class BaseInitData(
    @Autowired
    @Lazy
    private val self: BaseInitData,
    private val postService: PostService,
    private val memberService: MemberService
) {

    @Bean
    fun initData(): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments ->
            self.work1()
            self.work2()
        }
    }

    @Transactional
    fun work2() {
        if (postService.count() > 0) {
            return
        }

        val author1 = memberService.findByUsername("user1").getOrThrow()
        val author2 = memberService.findByUsername("user2").getOrThrow()

        val post1 = postService.write(author1, "제목1", "내용1")
        val post2 = postService.write(author1, "제목2", "내용2")
        postService.write(author2, "제목3", "내용3")

        post1.addComment(author1, "댓글 1-1")
        post1.addComment(author1, "댓글 1-2")
        post1.addComment(author1, "댓글 1-3")
        post2.addComment(author2, "댓글 2-1")
        post2.addComment(author2, "댓글 2-2")
    }

    @Transactional
    fun work1() {
        if (memberService.count() > 0) {
            return
        }

        memberService.join("system", "system", "시스템", "system")
        memberService.join("admin", "admin", "운영자", "admin")
        memberService.join("user1", "1234", "유저1", "user1")
        memberService.join("user2", "1234", "유저2", "user2")
        memberService.join("user3", "1234", "유저3", "user3")
    }
}
