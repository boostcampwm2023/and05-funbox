package com.rpg.funbox.data

import com.rpg.funbox.app.MainApplication
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

object JWTInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        //val accessToken: String = MainApplication.mySharedPreferences.getJWT("jwt", "")
        val accessToken: String = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjIsImlhdCI6MTcwMTMxODg1NCwiZXhwIjoxNzAxNTc4MDU0fQ.TAb6RbYavU6X2PSar7s1br-AQaWGhOuTFlTZ8AJCZpk"
        Timber.d("JWTInterceptor: $accessToken")
        val newRequest = request().newBuilder()
            .addHeader("authorization", accessToken)
            .build()
        proceed(newRequest)
    }
}