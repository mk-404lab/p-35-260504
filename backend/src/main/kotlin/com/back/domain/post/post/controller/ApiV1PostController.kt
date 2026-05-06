package com.back.domain.post.post.controller

import com.back.domain.post.post.dto.PostDto
import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.service.PostService
import com.back.global.extentions.getOrThrow
import com.back.global.rq.Rq
import com.back.global.rsData.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.MediaType
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "ApiV1PostController", description = "글 API, 인증의 경우 헤더가 쿠키보다 우선한다.")
@SecurityRequirement(name = "bearerAuth")
class ApiV1PostController(
    private val postService: PostService,
    private val rq: Rq
) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "글 다건 조회")
    fun list(): List<PostDto> {
        val result = postService.findAll()

        val postDtoList = result.reversed()
            .map { post: Post -> PostDto(post) }

        return postDtoList
    }

    @GetMapping("/{id}")
    @Operation(summary = "글 단건 조회")
    fun detail(@PathVariable id: Int): PostDto {
        val post = postService.findById(id).getOrThrow()
        return PostDto(post)
    }

    data class PostWriteReqBody(
        val title: @Size(
            min = 2,
            max = 10,
            message = "03-title-제목은 2자 이상 10자 이하로 입력해주세요."
        ) @NotBlank(message = "01-title-제목은 필수입니다.") String,

        val content: @NotBlank(message = "02-content-내용은 필수입니다.") @Size(
            min = 2,
            max = 100,
            message = "04-content-내용은 2자 이상 100자 이하로 입력해주세요."
        ) String
    )

    data class PostWriteResBody(
        val postDto: PostDto?
    )

    @PostMapping
    @Operation(summary = "글 작성")
    fun write(
        @RequestBody @Valid reqBody: @Valid PostWriteReqBody
    ): RsData<PostWriteResBody> {
        val actor = rq.actor // 인증된 사용자 정보 가져오기

        val post = postService.write(actor, reqBody.title, reqBody.content)

        return RsData(
            "${post.id}번 게시물이 생성되었습니다.",
            "201-1",
            PostWriteResBody(
                PostDto(post)
            )
        )
    }


     data class PostModifyReqBody(
        val title: @Size(
            min = 2,
            max = 10,
            message = "03-title-제목은 2자 이상 10자 이하로 입력해주세요."
        ) @NotBlank(message = "01-title-제목은 필수입니다.") String,

        val content: @NotBlank(message = "02-content-내용은 필수입니다.") @Size(
            min = 2,
            max = 100,
            message = "04-content-내용은 2자 이상 100자 이하로 입력해주세요."
        ) String
    )

    data class PostModifyResBody(
        val postDto: PostDto
    )

    @PutMapping("/{id}")
    @Operation(summary = "글 수정")
    @Transactional
    fun modify(
        @PathVariable id: Int,
        @RequestBody @Valid reqBody: @Valid PostModifyReqBody
    ): RsData<PostModifyResBody> {
        val actor = rq.actor // 인증된 사용자 정보 가져오기

        val post = postService.findById(id).getOrThrow()
        post.checkModify(actor)

        postService.modify(id, reqBody.title, reqBody.content)

        return RsData(
            "${post.id}번 게시물이 수정되었습니다.",
            "200-1",
            PostModifyResBody(
                PostDto(post)
            )
        )
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "글 삭제")
    @Transactional
    fun delete(
        @PathVariable id: Int
    ): RsData<Void?> {
        val actor = rq.actor // 인증된 사용자 정보 가져오기

        val post = postService.findById(id).getOrThrow()
        println(post.author.id)
        post.checkDelete(actor)

        postService.deleteById(id)

        return RsData(
            "${id}번 게시물이 삭제되었습니다.",
            "200-1"
        )
    }
}
