package com.rpg.funbox.data.dto

import com.squareup.moshi.Json

data class UserAuthDto(
    @field:Json(name = "id")
    val id: Int,

    @field:Json(name = "username")
    val userName: String?,

    @field:Json(name = "iat")
    val iat: Int,

    @field:Json(name = "exp")
    val exp: Int
)
