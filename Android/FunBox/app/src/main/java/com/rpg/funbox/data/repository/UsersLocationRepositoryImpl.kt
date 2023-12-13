package com.rpg.funbox.data.repository

import com.rpg.funbox.data.RetrofitInstance
import com.rpg.funbox.data.dto.UserLocationResponse
import com.rpg.funbox.data.dto.UsersLocationRequest
import com.rpg.funbox.data.network.service.UsersLocationApi
import timber.log.Timber

class UsersLocationRepositoryImpl : UsersLocationRepository {

    private val usersLocationApi: UsersLocationApi by lazy {
        RetrofitInstance.retrofit.create(UsersLocationApi::class.java)
    }

    override suspend fun getUsersLocation(locX: Double, locY: Double): UserLocationResponse {
        val response = usersLocationApi.fetchUsersLocation(UsersLocationRequest(locX = locX, locY = locY))
        Timber.d("Response Code: ${response.code()}")
        when (response.code()) {
            in successStatusCodeRange -> {
                Timber.d("Success")
                return UserLocationResponse("OK", response.body())
            }

            UNAUTHORIZED_STATUS -> {
                Timber.d("Unauthorized")
                return UserLocationResponse("Unauthorized", null)
            }

            in serverErrorStatusCodeRange -> {
                Timber.d("Server Error")
                return UserLocationResponse("Server Error", null)
            }

            else -> {}
        }

        Timber.d("Else")
        return UserLocationResponse("Timeout", null)
    }

    companion object {
        private const val UNAUTHORIZED_STATUS: Int = 401

        private val successStatusCodeRange: IntRange = 200..299
        private val serverErrorStatusCodeRange: IntRange = 500..599
    }
}