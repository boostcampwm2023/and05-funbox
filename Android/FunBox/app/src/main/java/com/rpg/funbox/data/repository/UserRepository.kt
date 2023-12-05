package com.rpg.funbox.data.repository

import com.rpg.funbox.data.dto.UserInfoResponse
import okhttp3.MultipartBody

interface UserRepository {

    suspend fun getUserInfo(): UserInfoResponse?

    suspend fun getSpecificUserInfo(userId: Int): UserInfoResponse?

    suspend fun patchUserName(userName: String): Boolean

    suspend fun postUserProfile(imageFile: MultipartBody.Part): Boolean
}