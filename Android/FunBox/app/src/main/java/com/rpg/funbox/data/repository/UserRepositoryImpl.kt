package com.rpg.funbox.data.repository

import com.rpg.funbox.data.RetrofitInstance
import com.rpg.funbox.data.dto.UserInfoRequest
import com.rpg.funbox.data.network.service.SignUpApi
import timber.log.Timber

class UserRepositoryImpl : UserRepository {

    private val signUpApi: SignUpApi by lazy {
        RetrofitInstance.retrofit.create(SignUpApi::class.java)
    }

    override suspend fun patchUserName(userName: String): Boolean {
        val response = signUpApi.submitUserName(UserInfoRequest(userName, null, null))
        Timber.d("Code: ${response.code()}")
        when (response.code()) {
            in successStatusCodeRange -> {
                Timber.d("Post UserName: ${response.body()}")
                return true
            }

            else -> {}
        }

        return false
    }

    override suspend fun postUserInfo(userName: String, profileUrl: String): Boolean {
        val response = signUpApi.submitUserInfo(UserInfoRequest(userName, profileUrl, null))
        return true
    }

    override suspend fun getUserLocation(): String {
        return ""
    }

    companion object {
        private const val UNAUTHORIZED_STATUS: Int = 401

        private val successStatusCodeRange: IntRange = 200..299
        private val serverErrorStatusCodeRange: IntRange = 500..599
    }
}