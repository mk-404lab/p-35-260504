package com.back.domain.post.post.repository

import com.back.standard.enums.PostSearchKeywordType
import com.back.standard.enums.PostSearchSortType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostRepositoryTest {
    @Autowired
    private lateinit var postRepository: PostRepository

    @Test
    @DisplayName("findQPagedByKw")
    fun t5() {
        val postPage = postRepository.findQPagedByKw(
            PostSearchKeywordType.AUTHOR_NICKNAME,
            "유저",
            PageRequest.of(
                0,
                10,
                PostSearchSortType.ID.sortBy
            ),
        )

        val content = postPage.content

        assertThat(content).isNotEmpty
    }
}