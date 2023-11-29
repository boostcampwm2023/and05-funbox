package com.rpg.funbox.data.network.service

import com.rpg.funbox.data.dto.UserInfoRequest
import com.rpg.funbox.data.dto.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface SignUpApi {

    @PATCH("/users/username")
    suspend fun submitUserName(
        @Body body: UserInfoRequest
    ): Response<UserInfoResponse>

    @POST("/users/profile")
    suspend fun submitUserInfo(
        @Body body: UserInfoRequest
    ): Response<UserInfoResponse>
}