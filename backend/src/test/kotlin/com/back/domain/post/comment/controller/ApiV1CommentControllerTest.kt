//package com.back.domain.post.comment.controller
//
//import com.back.domain.member.entity.Member
//import com.back.domain.member.repository.MemberRepository
//import com.back.domain.post.comment.entity.Comment
//import com.back.domain.post.post.repository.PostRepository
//import com.back.global.extentions.getOrThrow
//import org.assertj.core.api.Assertions
//import org.hamcrest.Matchers
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
//import org.springframework.http.MediaType
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers
//import org.springframework.transaction.annotation.Transactional
//
//@SpringBootTest
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//@Transactional
//class ApiV1CommentControllerTest(
//    private val mvc: MockMvc,
//    private val postRepository: PostRepository,
//    private val memberRepository: MemberRepository
//) {
//
//
//    @Test
//    @DisplayName("댓글 다건 조회 - 1번 글에 대한 댓글")
//    @Throws(Exception::class)
//    fun t1() {
//        val targetPostId = 1
//
//        val resultActions = mvc!!
//            .perform(
//                MockMvcRequestBuilders.get("/api/v1/posts/${targetPostId}/comments")
//            )
//            .andDo(MockMvcResultHandlers.print())
//
//        resultActions
//            .andExpect(MockMvcResultMatchers.status().isOk())
//
//        resultActions
//            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
//            .andExpect(
//                MockMvcResultMatchers.jsonPath(
//                    "$[*].id",
//                    Matchers.containsInRelativeOrder<Int?>(3, 1)
//                )
//            )
//            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(3))
//            .andExpect(MockMvcResultMatchers.jsonPath("$[0].createDate").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("$[0].modifyDate").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("$[0].content").value("댓글 1-3"))
//            .andExpect(MockMvcResultMatchers.jsonPath("$[0].authorId").value(3))
//            .andExpect(MockMvcResultMatchers.jsonPath("$[0].authorName").value("유저1"))
//    }
//
//    @Test
//    @DisplayName("댓글 단건 조회 - 1번 글의 1번 댓글")
//    @Throws(Exception::class)
//    fun t2() {
//        val targetPostId = 1
//        val targetCommentId = 1
//
//        val resultActions = mvc
//            .perform(
//                MockMvcRequestBuilders.get("/api/v1/posts/${targetPostId}/comments/${targetCommentId}")
//            )
//            .andDo(MockMvcResultHandlers.print())
//
//        resultActions
//            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1CommentController::class.java))
//            .andExpect(MockMvcResultMatchers.handler().methodName("detail"))
//            .andExpect(MockMvcResultMatchers.status().isOk())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.createDate").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.modifyDate").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("댓글 1-1"))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.authorId").value(3))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value("유저1"))
//    }
//
//    @Test
//    @DisplayName("댓글 생성 - 1번 글에 생성")
//    @Throws(Exception::class)
//    fun t3() {
//        val targetPostId = 1
//        val content = "새로운 댓글"
//        val author: Member = memberRepository!!.findByUsername("user1").getOrThrow()
//
//        val resultActions = mvc!!
//            .perform(
//                MockMvcRequestBuilders.post("/api/v1/posts/%d/comments".formatted(targetPostId))
//                    .header("Authorization", "Bearer %s".formatted(author.apiKey))
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(
//                        """
//                                        {
//                                            "content": "%s"
//                                        }
//
//                                        """.trimIndent().formatted(content)
//                    )
//            )
//            .andDo(MockMvcResultHandlers.print())
//
//        resultActions
//            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1CommentController::class.java))
//            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
//            .andExpect(MockMvcResultMatchers.status().isCreated())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("201-1"))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("6번 댓글이 생성되었습니다."))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.commentDto.id").value(6))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.commentDto.createDate").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.commentDto.modifyDate").exists())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.commentDto.content").value(content))
//    }
//
//    @Test
//    @DisplayName("댓글 수정 - 1번 글의 1번 댓글 수정")
//    @Throws(Exception::class)
//    fun t4() {
//        val targetPostId = 1
//        val targetCommentId = 1
//        val content = "댓글 내용 수정"
//        val author: Member = memberRepository!!.findByUsername("user1").get()
//
//        val resultActions = mvc!!
//            .perform(
//                MockMvcRequestBuilders.put("/api/v1/posts/%d/comments/%d".formatted(targetPostId, targetCommentId))
//                    .header("Authorization", "Bearer %s".formatted(author.apiKey))
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(
//                        """
//                                        {
//                                            "content": "%s"
//                                        }
//
//                                        """.trimIndent().formatted(content)
//                    )
//            )
//            .andDo(MockMvcResultHandlers.print())
//
//        resultActions
//            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1CommentController::class.java))
//            .andExpect(MockMvcResultMatchers.handler().methodName("modify"))
//            .andExpect(MockMvcResultMatchers.status().isOk())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("%d번 댓글이 수정되었습니다.".formatted(targetCommentId)))
//
//        val post = postRepository!!.findById(targetPostId).get()
//        val comment = post.findCommentById(targetCommentId).get()
//
//        Assertions.assertThat(comment.content).isEqualTo(content)
//    }
//
//    @Test
//    @DisplayName("댓글 삭제 - 1번 글의 1번 댓글 삭제")
//    @Throws(Exception::class)
//    fun t5() {
//        val targetPostId = 1
//        val targetCommentId = 1
//        val author: Member = memberRepository!!.findByUsername("user1").get()
//
//        val resultActions = mvc!!
//            .perform(
//                MockMvcRequestBuilders.delete("/api/v1/posts/%d/comments/%d".formatted(targetPostId, targetCommentId))
//                    .header("Authorization", "Bearer %s".formatted(author.apiKey))
//            )
//            .andDo(MockMvcResultHandlers.print())
//
//        // 필수 검증
//        resultActions
//            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1CommentController::class.java))
//            .andExpect(MockMvcResultMatchers.handler().methodName("delete"))
//            .andExpect(MockMvcResultMatchers.status().isOk())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("%d번 댓글이 삭제되었습니다.".formatted(targetCommentId)))
//
//        // 선택적 검증
//        val post = postRepository!!.findById(targetPostId).orElse(null)
//        val comment: Comment = post.findCommentById(targetCommentId).orElse(null)!!
//        Assertions.assertThat<Comment?>(comment).isNull()
//    }
//
//    @Test
//    @DisplayName("댓글 수정 - 다른 작성자의 댓글 수정")
//    @Throws(Exception::class)
//    fun t6() {
//        val targetPostId: Long = 1
//        val targetCommentId: Long = 1
//        val content = "댓글 내용 수정"
//
//        val author: Member = memberRepository!!.findByUsername("user2").get()
//
//        val resultActions = mvc!!
//            .perform(
//                MockMvcRequestBuilders.put("/api/v1/posts/%d/comments/%d".formatted(targetPostId, targetCommentId))
//                    .header("Authorization", "Bearer %s".formatted(author.apiKey))
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(
//                        """
//                                        {
//                                            "content": "%s"
//                                        }
//
//                                        """.trimIndent().formatted(content)
//                    )
//            )
//            .andDo(MockMvcResultHandlers.print())
//
//        resultActions
//            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1CommentController::class.java))
//            .andExpect(MockMvcResultMatchers.handler().methodName("modify"))
//            .andExpect(MockMvcResultMatchers.status().isForbidden())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("403-1"))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("댓글 수정 권한이 없습니다."))
//    }
//
//    @Test
//    @DisplayName("댓글 삭제 - 다른 작성자의 댓글 삭제")
//    @Throws(Exception::class)
//    fun t7() {
//        val targetPostId: Long = 1
//        val targetCommentId: Long = 1
//
//        val author: Member = memberRepository!!.findByUsername("user2").get()
//
//        val resultActions = mvc!!
//            .perform(
//                MockMvcRequestBuilders.delete("/api/v1/posts/%d/comments/%d".formatted(targetPostId, targetCommentId))
//                    .header("Authorization", "Bearer %s".formatted(author.apiKey))
//            )
//            .andDo(MockMvcResultHandlers.print())
//
//        // 필수 검증
//        resultActions
//            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1CommentController::class.java))
//            .andExpect(MockMvcResultMatchers.handler().methodName("delete"))
//            .andExpect(MockMvcResultMatchers.status().isForbidden())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("403-2"))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("댓글 삭제 권한이 없습니다."))
//    }
//}