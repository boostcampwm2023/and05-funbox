package com.rpg.funbox.data.repository

import com.rpg.funbox.data.dto.UserAuthDto

interface NaverLoginRepository {

    suspend fun postNaverAccessToken(token: String): UserAuthDto?
}