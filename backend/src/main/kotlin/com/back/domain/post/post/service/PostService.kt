package com.back.domain.post.post.service

import com.back.domain.member.entity.Member
import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.repository.PostRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository
) {

    @Transactional
    fun write(author: Member, title: String, content: String): Post {
        val post = Post(author, title, content)
        return postRepository.save<Post>(post)
    }

    fun modify(id: Int, title: String, content: String): Post =
        postRepository.findById(id).get().apply {
            update(title, content)
        }


    fun deleteById(id: Int) =
        postRepository.deleteById(id)


    fun findById(id: Int): Post? =
        postRepository.findByIdOrNull(id)

    fun count(): Long =
        postRepository.count()


    fun findAll(): List<Post> =
        postRepository.findAll()

    fun flush() =
        postRepository.flush()

}

