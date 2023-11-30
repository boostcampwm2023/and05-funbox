package com.rpg.funbox.data

import okhttp3.OkHttpClient

object OkHttpClientInstance {

    val okHttpClient: OkHttpClient by lazy {
        // val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder().run {
            this.addInterceptor(JWTInterceptor)
            this.build()
        }
    }
}