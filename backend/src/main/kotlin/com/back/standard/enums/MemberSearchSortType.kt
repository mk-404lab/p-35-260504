package com.back.standard.enums

enum class MemberSearchSortType {
    ID,
    ID_ASC,
    USERNAME,
    USERNAME_ASC,
    NICKNAME,
    NICKNAME_ASC;

    val property: String
        get() = name.removeSuffix("_ASC").lowercase()

    val isAscending: Boolean
        get() = this.name.endsWith("_ASC")

}