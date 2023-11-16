package com.example.funbox.data.network.service

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface LoadUserService {
    @GET("/search")
    fun search(
        @Query("locX") locX : Double,
        @Query("locY") locY : Double,
    ) : Call<String>
}