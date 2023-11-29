package com.rpg.funbox.data.dto

import com.squareup.moshi.Json

data class UserInfoRequest(
    @field:Json(name = "username")
    val userName: String,

    @field:Json(name = "profileUrl")
    val profileUrl: String?,

    @field:Json(name = "id")
    val message: String?
)