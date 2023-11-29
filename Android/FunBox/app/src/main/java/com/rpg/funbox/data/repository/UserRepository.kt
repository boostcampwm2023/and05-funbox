package com.rpg.funbox.data.repository

interface UserRepository {

    suspend fun patchUserName(userName: String): Boolean

    suspend fun postUserInfo(userName: String, profileUrl: String): Boolean

    suspend fun getUserLocation(): String
}