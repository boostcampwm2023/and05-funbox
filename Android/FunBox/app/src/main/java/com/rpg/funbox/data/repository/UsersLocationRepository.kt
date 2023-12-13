package com.rpg.funbox.data.repository

import com.rpg.funbox.data.dto.UserLocationResponse

interface UsersLocationRepository {

    suspend fun getUsersLocation(locX: Double, locY: Double): UserLocationResponse
}