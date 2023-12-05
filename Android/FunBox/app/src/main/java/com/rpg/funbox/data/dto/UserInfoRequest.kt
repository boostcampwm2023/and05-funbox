package com.rpg.funbox.data.dto

import com.squareup.moshi.Json

data class UserInfoRequest(
    @Json(name = "username")
    val userName: String?,

    @Json(name = "profileUrl")
    val profileUrl: String?,

    @Json(name = "id")
    val message: String?
)