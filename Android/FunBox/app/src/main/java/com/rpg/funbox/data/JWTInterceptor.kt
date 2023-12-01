package com.rpg.funbox.data

import com.rpg.funbox.app.MainApplication
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

object JWTInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val accessToken: String = MainApplication.mySharedPreferences.getJWT("jwt", "")
        Timber.d("JWTInterceptor: $accessToken")
        val newRequest = request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        proceed(newRequest)
    }
}
