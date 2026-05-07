package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import com.back.domain.member.entity.QMember
import com.querydsl.jpa.impl.JPAQueryFactory

class MemberRepositoryImpl (
    private val jpaQueryFactory: JPAQueryFactory
) : MemberRepositoryCustom {

    override fun findQById(id: Int): Member? {

        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.id.eq(id))    // where member.id = id
            .fetchOne()     //limit 1
    }

    override fun findQByUsername(username: String): Member? {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetchOne() // limit 1
    }

    override fun findQByIdIn(ids: List<Int>): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.id.`in` (ids))
            .fetch()
    }

    override fun findQByUsernameAndNickname(username: String, nickname: String): Member? {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .and(member.nickname.eq(nickname))
            )
            .fetchOne()
    }

    override fun findQByUsernameOrNickname(username: String, nickname: String): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .or(member.nickname.eq(nickname))
            )
            .fetch()
    }

    override fun findQByUsernameAndEitherPasswordOrNickname(
        username: String,
        password: String,
        nickname: String
    ): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.username.eq(username)
                .and(member.password.eq(password)
                    .or(member.nickname.eq(nickname))))
            .fetch()
    }

    override fun findQByNicknameContaining(nickname: String): List<Member> {
        // where nickname LIKE %a%
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .fetch()
    }

    override fun countQByNicknameContaining(nickname: String): Long {
        val member = QMember.member

        return jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(member.nickname.contains(nickname))
            .fetchOne() ?: 0L
        /*
        fetchOne()은 쿼리 결과가 없을 경우 null을 반환함
        만약 member.count 결과가 존재하지 않으면 null이 넘어올 수도 있음
        하지만 우리는 Long 타입의 반환을 지정했으며, 실제로 해당하는 결과가 없으면 집계 함수에 따라
        0이 나오기를 기대함
        따라서 엘비스를 사용해 null일 경우 0L으로 설정
         */
    }

    override fun existsQByNicknameContaining(nickname: String): Boolean {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .fetchFirst() != null
    }
}