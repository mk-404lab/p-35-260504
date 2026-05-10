package com.back.domain.member.repository

import com.back.domain.member.entity.Member
import com.back.domain.member.entity.QMember
import com.back.standard.enums.MemberSearchKeywordType
import com.back.standard.enums.MemberSearchSortType
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.hibernate.Session
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils


class MemberRepositoryImpl (
    private val jpaQueryFactory: JPAQueryFactory
) : MemberRepositoryCustom {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun findByUsername(username: String): Member? {
        val session = entityManager.unwrap(Session::class.java)

        return session
            .byNaturalId(Member::class.java)
            .using("username", username)
            .load()
    }

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

    override fun findQByNicknameContaining(nickname: String, pageable: Pageable): Page<Member> {
        val member = QMember.member

        // content 쿼리
        val result = jpaQueryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageableExecutionUtils.getPage(
            result,
            pageable
        ) {
            jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(member.nickname.contains(nickname))
                .fetchOne() ?: 0L
        }
    }

    override fun findQByNicknameContainingOrderByIdDesc(nickname: String): List<Member> {
        val member = QMember.member

        return jpaQueryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .orderBy(member.id.desc())
            .fetch()
    }

    override fun findQByUsernameContaining(username: String, pageable: Pageable): Page<Member> {

        val member = QMember.member

        val query = jpaQueryFactory
            .selectFrom(member)
            .where(member.username.contains(username))

        pageable.sort.forEach { order ->
            when (order.property) {
                "id" -> query.orderBy(if (order.isAscending) member.id.asc() else member.id.desc())
                "username" -> query.orderBy(if (order.isAscending) member.username.asc() else member.username.desc())
                "nickname" -> query.orderBy(if (order.isAscending) member.nickname.asc() else member.nickname.desc())
            }
        }

        val content = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageableExecutionUtils.getPage(content, pageable) {
            jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(member.nickname.contains(username))
                .fetchOne() ?: 0L
        }


    }

    override fun findByKwPaged(kw: String, kwType: MemberSearchKeywordType, pageable: Pageable): Page<Member> {
        val member = QMember.member

        val builder = BooleanBuilder().apply {
            when (kwType) {
                MemberSearchKeywordType.USERNAME -> this.and(member.username.contains(kw))
                MemberSearchKeywordType.NICKNAME -> this.and(member.nickname.contains(kw))
                MemberSearchKeywordType.ALL -> this.and(
                    member.username.contains(kw).or(
                        member.nickname.contains(kw)
                    )
                )
            }
        }

//        val order1 = OrderSpecifier(
//            Order.ASC,  // ASC, DESC의 정렬 방법(order)을 넣음
//            member.id   // 정렬 기준(target)을 정함
//        )

        val query = jpaQueryFactory
            .selectFrom(member)
            .where(builder)

        pageable.sort.forEach { order ->
            val path = when(order.property.lowercase()) {
                MemberSearchSortType.ID.property -> member.id
                MemberSearchSortType.USERNAME.property -> member.username
                MemberSearchSortType.NICKNAME.property -> member.nickname
                else -> null
            }

            path?.let {property ->
                OrderSpecifier(
                    if(order.isAscending) Order.ASC else Order.DESC,
                    property
                ).also {
                    query.orderBy(it)
                }
            }
        }



        val content = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageableExecutionUtils.getPage(content, pageable) {
            jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(builder)
                .fetchOne() ?: 0L
        }
    }
}

