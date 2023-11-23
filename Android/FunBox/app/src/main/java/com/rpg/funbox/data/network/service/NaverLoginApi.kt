package com.rpg.funbox.data.network.service

import com.rpg.funbox.data.dto.NaverAccessTokenRequest
import com.rpg.funbox.data.dto.NaverLoginRequest
import com.rpg.funbox.data.dto.User
import retrofit2.http.Body
import retrofit2.http.POST

interface NaverLoginApi {

    @POST("/")
    suspend fun tryNaverLogin(
        @Body body: NaverLoginRequest
    ): User

    @POST("/")
    suspend fun submitNaverAccessToken(
        @Body body: NaverAccessTokenRequest
    ): User
}