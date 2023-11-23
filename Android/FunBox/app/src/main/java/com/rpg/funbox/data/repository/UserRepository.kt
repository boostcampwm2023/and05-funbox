package com.rpg.funbox.data.repository

interface UserRepository {

    suspend fun getUserLocation(): String
}