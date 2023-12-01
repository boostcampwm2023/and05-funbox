package com.rpg.funbox.data.repository

import com.rpg.funbox.data.RetrofitInstance
import com.rpg.funbox.data.dto.UserLocation
import com.rpg.funbox.data.dto.UsersLocationRequest
import com.rpg.funbox.data.network.service.UsersLocationApi

class UsersLocationRepositoryImpl : UsersLocationRepository {

    private val usersLocationApi: UsersLocationApi by lazy {
        RetrofitInstance.retrofit.create(UsersLocationApi::class.java)
    }

    override suspend fun getUsersLocation(locX: Double, locY: Double): List<UserLocation>? {
        val response = usersLocationApi.fetchUsersLocation(UsersLocationRequest(locX = locX, locY = locY))

        when (response.code()) {
            in successStatusCodeRange -> {
                return response.body()
            }

            UNAUTHORIZED_STATUS -> {}

            in serverErrorStatusCodeRange -> {}

            else -> {}
        }

        return null
    }

    companion object {
        private const val UNAUTHORIZED_STATUS: Int = 401

        private val successStatusCodeRange: IntRange = 200..299
        private val serverErrorStatusCodeRange: IntRange = 500..599
    }
}