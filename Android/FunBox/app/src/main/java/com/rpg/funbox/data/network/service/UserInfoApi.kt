package com.rpg.funbox.data.network.service

import com.rpg.funbox.data.dto.UserInfoRequest
import com.rpg.funbox.data.dto.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface UserInfoApi {

    @GET("/users/{id}")
    suspend fun fetchSpecificUser(
        @Path(value = "id") userId: Int
    ): Response<UserInfoResponse>

    @PATCH("/users/message")
    suspend fun submitUserMessage(
        @Body body: UserInfoRequest
    ): Response<UserInfoResponse>
}