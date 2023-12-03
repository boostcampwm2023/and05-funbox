package com.rpg.funbox.data.dto

import com.squareup.moshi.Json

data class UserAuthDto(
    @Json(name = "id")
    val id: Int,

    @Json(name = "iat")
    val iat: Int,

    @Json(name = "exp")
    val exp: Int
)
