package com.rpg.funbox.data.repository

import com.rpg.funbox.app.MainApplication
import com.rpg.funbox.data.JwtDecoder
import com.rpg.funbox.data.RetrofitInstance
import com.rpg.funbox.data.dto.NaverAccessTokenRequest
import com.rpg.funbox.data.dto.UserAuthDto
import com.rpg.funbox.data.network.service.NaverLoginApi
import timber.log.Timber

class NaverLoginRepositoryImpl : NaverLoginRepository {

    private val naverLoginApi: NaverLoginApi by lazy {
        RetrofitInstance.retrofit.create(NaverLoginApi::class.java)
    }

    override suspend fun postNaverAccessToken(token: String): UserAuthDto? {
        val response = naverLoginApi.submitNaverAccessToken(NaverAccessTokenRequest(token))
        Timber.d("${response.code()}")
        val body = response.body()
        Timber.d("$body")
        when (response.code()) {
            in successStatusCodeRange -> {
                return body?.let {
                    Timber.d("JWT: ${it.accessToken}")
                    MainApplication.mySharedPreferences.setJWT("jwt", it.accessToken)
                    JwtDecoder.getUser(it.accessToken)
                }
            }

            UNAUTHORIZED_STATUS -> {
                if (body != null) {
                    Timber.d("JWT: ${body.accessToken}")
                }
                return UserAuthDto(0, 0, 0)
            }

            in serverErrorStatusCodeRange -> {}

            else -> {}
        }
        return null
    }

    companion object {
        private const val UNAUTHORIZED_STATUS: Int = 401

        private val successStatusCodeRange: IntRange = 200..299
        private val serverErrorStatusCodeRange: IntRange = 500..599
    }
}