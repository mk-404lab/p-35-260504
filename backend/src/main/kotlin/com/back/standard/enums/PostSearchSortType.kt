package com.back.standard.enums

import org.springframework.data.domain.Sort

enum class PostSearchSortType {
    ID,
    ID_ASC;

    val sortBy by lazy {
        Sort.by(
            if (isAsc) Sort.Direction.ASC else Sort.Direction.DESC,
            property
        )
    }

    val property by lazy {
        name.removeSuffix("_ASC").lowercase()
    }

    val isAsc by lazy {
        name.endsWith("_ASC")
    }
}