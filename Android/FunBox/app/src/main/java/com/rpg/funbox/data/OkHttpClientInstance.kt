package com.rpg.funbox.data

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttpClientInstance {

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().run {
            this.addInterceptor(JWTInterceptor)
            this.build()
        }
    }
}