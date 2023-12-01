package com.rpg.funbox.data.repository

import okhttp3.MultipartBody

interface UserRepository {

    suspend fun getUserInfo(): String?

    suspend fun patchUserName(userName: String): Boolean

    suspend fun postUserProfile(imageFile: MultipartBody.Part): Boolean
}