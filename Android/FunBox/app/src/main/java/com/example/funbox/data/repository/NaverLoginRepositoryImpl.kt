package com.example.funbox.data.repository

import com.example.funbox.data.RetrofitInstance
import com.example.funbox.data.dto.NaverAccessTokenRequest
import com.example.funbox.data.dto.NaverLoginRequest
import com.example.funbox.data.dto.User
import com.example.funbox.data.network.service.NaverLoginApi

class NaverLoginRepositoryImpl : NaverLoginRepository {

    private val naverLoginApi: NaverLoginApi by lazy {
        RetrofitInstance.retrofit.create(NaverLoginApi::class.java)
    }

    override suspend fun postNaverProfileUserId(userId: String): User? {
        val response = naverLoginApi.tryNaverLogin(NaverLoginRequest(userId))
        when (response.status) {
            in successStatusCodeRange -> {
                return response
            }

            UNAUTHORIZED_STATUS -> {
                return null
            }

            in serverErrorStatusCodeRange -> {
                return null
            }

            else -> {}
        }

        return null
    }

    override suspend fun postNaverAccessToken(token: String): User? {
        val response = naverLoginApi.submitNaverAccessToken(NaverAccessTokenRequest(token))
        return when (response.name) {
            null -> {
                response
            }

            else -> {
                null
            }
        }
    }

    companion object {
        private const val UNAUTHORIZED_STATUS: Int = 401

        private val successStatusCodeRange: IntRange = 200..299
        private val serverErrorStatusCodeRange: IntRange = 500..599
    }
}