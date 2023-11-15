package com.example.funbox.network.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoadUserService {
    @GET("/login")
    fun login(
        @Query("locX") locX : Double,
        @Query("locY") locY : Double,
    ) : Call<String>

    companion object{
        private const val BASE_URL = "BaseURL"
        private val gson =
            GsonBuilder()
                .setLenient()
                .create()

        fun create() : LoadUserService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(LoadUserService::class.java)
        }
    }
}