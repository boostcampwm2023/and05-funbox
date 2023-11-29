package com.rpg.funbox.data.repository

import com.rpg.funbox.data.RetrofitInstance
import com.rpg.funbox.data.dto.UserLocation
import com.rpg.funbox.data.dto.UsersLocationRequest
import com.rpg.funbox.data.network.service.UsersLocationApi
import timber.log.Timber

class UsersLocationRepositoryImpl : UsersLocationRepository {

    private val usersLocationApi: UsersLocationApi by lazy {
        RetrofitInstance.retrofit.create(UsersLocationApi::class.java)
    }

    override suspend fun getUsersLocation(): List<UserLocation>? {
        val response = usersLocationApi.fetchUsersLocation(UsersLocationRequest(0.0, 0.0))

        when (response.code()) {
            in successStatusCodeRange -> {
                Timber.d("${response.body()}")
            }

            UNAUTHORIZED_STATUS -> {
                Timber.d("${response.code()}")
            }

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