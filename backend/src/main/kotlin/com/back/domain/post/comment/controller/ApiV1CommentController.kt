package com.back.domain.post.comment.controller

import com.back.domain.post.comment.dto.CommentDto
import com.back.domain.post.comment.entity.Comment
import com.back.domain.post.post.service.PostService
import com.back.global.extentions.getOrThrow
import com.back.global.rq.Rq
import com.back.global.rsData.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@Tag(name = "ApiV1CommentController", description = "댓글 API")
class ApiV1CommentController(
    private val postService: PostService,
    private val rq: Rq
) {

    @GetMapping
    @Operation(summary = "댓글 다건 조회")
    fun list(
        @PathVariable postId: Int
    ): List<CommentDto> {
        val post = postService.findById(postId).getOrThrow()
        val comments = post.comments

        return comments.reversed()
            .map{ comment: Comment -> CommentDto(comment) }
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "글 단건 조회")
    fun detail(@PathVariable postId: Int, @PathVariable commentId: Int): CommentDto {
        val post = postService.findById(postId).getOrThrow()
        val comment = post.findCommentById(commentId).get()

        return CommentDto(comment)
    }


    data class CommentWriteReqBody(
        val content: @NotBlank(message = "02-content-내용은 필수입니다.") @Size(
            min = 2,
            max = 100,
            message = "04-content-내용은 2자 이상 100자 이하로 입력해주세요."
        ) String
    )

    data class CommentWriteResBody(
        val commentDto: CommentDto?,
        val totalCount: Int
    )

    @PostMapping
    @Transactional
    @Operation(summary = "댓글 작성")
    fun write(
        @PathVariable postId: Int,
        @RequestBody @Valid reqBody: @Valid CommentWriteReqBody
    ): RsData<CommentWriteResBody> {
        val actor = rq.actor
        val post = postService.findById(postId).getOrThrow()
        val comment = post.addComment(actor, reqBody.content)

        postService.flush()

        return RsData(
            "${comment.id}번 댓글이 생성되었습니다.",
            "201-1",
            CommentWriteResBody(
                CommentDto(comment),
                2
            )
        )
    }

    @DeleteMapping("/{commentId}")
    @Transactional
    @Operation(summary = "댓글 삭제")
    fun delete(
        @PathVariable postId: Int,
        @PathVariable commentId: Int
    ): RsData<CommentDto> {
        val actor = rq.actor

        val post = postService.findById(postId).getOrThrow()
        val comment = post.findCommentById(commentId).get()
        comment.checkActorDelete(actor)

        post.deleteComment(commentId)

        return RsData(
            "${commentId}번 댓글이 삭제되었습니다.",
            "200-1",
            CommentDto(comment)
        )
    }

    data class CommentModifyReqBody(
        val content: String
    )

    @PutMapping("/{commentId}")
    @Transactional
    @Operation(summary = "댓글 수정")
    fun modify(
        @PathVariable postId: Int,
        @PathVariable commentId: Int,
        @RequestBody reqBody: CommentModifyReqBody
    ): RsData<Void> {
        val actor = rq.actor

        val post = postService.findById(postId).getOrThrow()
        val comment = post.findCommentById(commentId).get()
        comment.checkActorModify(actor)

        post.modifyComment(commentId, reqBody.content)

        return RsData(
            "${comment.id}번 댓글이 수정되었습니다.",
            "200-1"
        )
    }
}
