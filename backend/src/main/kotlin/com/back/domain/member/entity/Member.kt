package com.back.domain.member.entity

import com.back.global.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
class Member(
    @Column(unique = true)
    var username: String,
    var password: String,
    var nickname: String,
    @Column(unique = true)
    var apiKey: String
) : BaseEntity(0) {

    constructor(id: Int, username: String, nickname: String)
            : this(username, "", nickname, "") {

        this.id = id
        this.username = username
        this.nickname = nickname
    }

    val name: String
        get() = this.nickname

    val isAdmin: Boolean
        get() = "admin" == username

    val authorities: List<SimpleGrantedAuthority>
        get() {
            return if (isAdmin) {
                listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
            } else {
                listOf(SimpleGrantedAuthority("ROLE_USER"))
            }
        }
}
