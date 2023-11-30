package com.rpg.funbox.data.repository

import com.rpg.funbox.data.RetrofitInstance
import com.rpg.funbox.data.dto.UserInfoRequest
import com.rpg.funbox.data.network.service.SignUpApi
import okhttp3.MultipartBody
import timber.log.Timber

class UserRepositoryImpl : UserRepository {

    private val signUpApi: SignUpApi by lazy {
        RetrofitInstance.retrofit.create(SignUpApi::class.java)
    }

    override suspend fun getUserInfo(): String? {
        val response = signUpApi.fetchUserInfo()
        when (response.code()) {
            in successStatusCodeRange -> {
                return response.body()?.username
            }

            else -> {}
        }

        return null
    }

    override suspend fun patchUserName(userName: String): Boolean {
        val response = signUpApi.submitUserName(UserInfoRequest(username = userName, null, null))
        Timber.d("Post User Name: ${response.code()}")
        when (response.code()) {
            in successStatusCodeRange -> {
                Timber.d("Post UserName: ${response.body()}")
                return true
            }

            else -> {}
        }

        return false
    }

    override suspend fun postUserProfile(imageFile: MultipartBody.Part): Boolean {
        val response = signUpApi.submitUserProfile(file = imageFile)
        Timber.d("Post User Profile: ${response.code()}")
        when (response.code()) {
            in successStatusCodeRange -> {
                return true
            }

            else -> {}
        }
        return false
    }

    companion object {
        private const val UNAUTHORIZED_STATUS: Int = 401

        private val successStatusCodeRange: IntRange = 200..299
        private val serverErrorStatusCodeRange: IntRange = 500..599
    }
}