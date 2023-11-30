package com.rpg.funbox.data.network.service

import com.rpg.funbox.data.dto.UserLocation
import com.rpg.funbox.data.dto.UsersLocationRequest
import com.rpg.funbox.data.dto.UsersLocationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UsersLocationApi {
    @POST("/users/location")
    suspend fun fetchUsersLocation(
        @Body body: UsersLocationRequest
    ) : Response<List<UserLocation>>
}