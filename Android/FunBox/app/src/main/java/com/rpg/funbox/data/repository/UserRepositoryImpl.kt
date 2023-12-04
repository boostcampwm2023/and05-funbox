package com.rpg.funbox.data.repository

import com.rpg.funbox.data.RetrofitInstance
import com.rpg.funbox.data.dto.UserInfoRequest
import com.rpg.funbox.data.dto.UserInfoResponse
import com.rpg.funbox.data.network.service.SignUpApi
import com.rpg.funbox.data.network.service.UserInfoApi
import okhttp3.MultipartBody

class UserRepositoryImpl : UserRepository {

    private val signUpApi: SignUpApi by lazy {
        RetrofitInstance.retrofit.create(SignUpApi::class.java)
    }

    private val userInfoApi: UserInfoApi by lazy {
        RetrofitInstance.retrofit.create(UserInfoApi::class.java)
    }

    override suspend fun getUserInfo(): UserInfoResponse? {
        val response = signUpApi.fetchUserInfo()
        when (response.code()) {
            in successStatusCodeRange -> {
                return response.body()
            }

            else -> {}
        }

        return null
    }

    override suspend fun getSpecificUserInfo(userId: Int): UserInfoResponse? {
        val response = userInfoApi.fetchSpecificUser(userId = userId)
        when (response.code()) {
            in successStatusCodeRange -> {
                return response.body()
            }

            else -> {}
        }

        return null
    }

    override suspend fun patchUserName(userName: String): Boolean {
        val response = signUpApi.submitUserName(UserInfoRequest(userName = userName, null, null))
        when (response.code()) {
            in successStatusCodeRange -> {
                return true
            }

            else -> {}
        }

        return false
    }

    override suspend fun postUserProfile(imageFile: MultipartBody.Part): Boolean {
        val response = signUpApi.submitUserProfile(file = imageFile)
        when (response.code()) {
            in successStatusCodeRange -> {
                return true
            }

            else -> {}
        }
        return false
    }

    override suspend fun withdraw(): Boolean {
        val response = signUpApi.deleteUserInfo()
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