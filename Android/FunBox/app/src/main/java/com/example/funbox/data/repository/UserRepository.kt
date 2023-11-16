package com.example.funbox.data.repository

interface UserRepository {

    suspend fun getUserLocation(): String
}