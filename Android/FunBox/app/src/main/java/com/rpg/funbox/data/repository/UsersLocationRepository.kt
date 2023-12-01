package com.rpg.funbox.data.repository

import com.rpg.funbox.data.dto.UserLocation

interface UsersLocationRepository {

    suspend fun getUsersLocation(locX: Double, locY: Double): List<UserLocation>?
}