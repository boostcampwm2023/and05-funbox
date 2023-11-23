package com.rpg.funbox.data.repository

import com.rpg.funbox.data.RetrofitInstance
import com.rpg.funbox.data.network.service.LoadUserService

class UserRepositoryImpl : UserRepository {
    private val userApi: LoadUserService by lazy {
        RetrofitInstance.retrofit.create(LoadUserService::class.java)
    }

    override suspend fun getUserLocation(): String {
        return ""
    }
}