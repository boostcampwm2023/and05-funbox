package com.rpg.funbox.data.network.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LoadUserService {
    @GET("/search")
    fun search(
        @Query("locX") locX : Double,
        @Query("locY") locY : Double,
    ) : Call<String>
}