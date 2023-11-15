package com.example.funbox.data.network.service

import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface LoadUserService {
    @GET("/login")
    fun login(
        @Query("locX") locX : Double,
        @Query("locY") locY : Double,
    ) : Call<String>

    companion object{
        private const val BASE_URL = "BaseURL"
        private val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        fun create() : LoadUserService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(LoadUserService::class.java)
        }
    }
}