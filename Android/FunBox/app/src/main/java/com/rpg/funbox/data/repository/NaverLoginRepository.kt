package com.rpg.funbox.data.repository

import com.rpg.funbox.data.dto.User

interface NaverLoginRepository {

    suspend fun postNaverProfileUserId(userId: String): User?

    suspend fun postNaverAccessToken(token: String): User?
}