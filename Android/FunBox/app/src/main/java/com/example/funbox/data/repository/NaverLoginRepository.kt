package com.example.funbox.data.repository

import com.example.funbox.data.dto.User

interface NaverLoginRepository {

    suspend fun postNaverProfileUserId(userId: String): User?

    suspend fun postNaverAccessToken(token: String): User?
}