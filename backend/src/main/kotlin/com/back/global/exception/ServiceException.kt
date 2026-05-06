package com.back.global.exception

import com.back.global.rsData.RsData

class ServiceException(
    private val resultCode: String,
    private val msg: String
) : RuntimeException(msg) {
    val rsData: RsData<Void>
        get() = RsData(
            msg,
            resultCode
        )
}
