package com.rpg.funbox.data.dto

import com.squareup.moshi.Json

data class UserInfoResponse(
    @field:Json(name = "username")
    val username: String?,

    @field:Json(name = "profileUrl")
    val profileUrl: String?,

    @field:Json(name = "id")
    val message: String?
)
