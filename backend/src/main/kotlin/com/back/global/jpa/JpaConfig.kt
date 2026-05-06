package com.back.global.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JpaConfig (

) {

    // EntityManager : DB 커넥션을 관리하고 실제 SQL을 실행하는 객체
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Bean
    fun jpaQuery(): JPAQueryFactory {
        // JPAQueryFactory : 내가 짠 코틀린 코드를 JPQL로 조립해주는 객체
        return JPAQueryFactory(entityManager)
    }

}