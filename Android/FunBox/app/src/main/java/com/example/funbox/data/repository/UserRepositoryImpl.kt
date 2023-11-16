package com.example.funbox.data.repository

import com.example.funbox.data.RetrofitInstance
import com.example.funbox.data.network.service.LoadUserService

class UserRepositoryImpl : UserRepository {
    private val userApi: LoadUserService by lazy {
        RetrofitInstance.retrofit.create(LoadUserService::class.java)
    }

    override suspend fun getUserLocation(): String {
        return ""
    }
}