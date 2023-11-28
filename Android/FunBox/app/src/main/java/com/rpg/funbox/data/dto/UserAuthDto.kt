package com.rpg.funbox.data.dto

data class UserAuthDto(
    val iat: Int,
    val exp: Int,
    val id: Int,
    val username: String?
)
