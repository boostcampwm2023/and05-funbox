package com.rpg.funbox.data.network.service

import com.rpg.funbox.data.dto.UserInfoRequest
import com.rpg.funbox.data.dto.UserInfoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface SignUpApi {

    @GET("/users")
    suspend fun fetchUserInfo(): Response<UserInfoResponse>

    @PATCH("/users/username")
    suspend fun submitUserName(
        @Body body: UserInfoRequest
    ): Response<UserInfoResponse>

    @Multipart
    @POST("/users/profile")
    suspend fun submitUserProfile(
        @Part file: MultipartBody.Part
    ): Response<UserInfoResponse>
}