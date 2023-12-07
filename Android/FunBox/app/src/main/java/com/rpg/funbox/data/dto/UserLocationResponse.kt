package com.rpg.funbox.data.dto

data class UserLocationResponse(
    val resultMessage: String,
    val userLocations: List<UserLocation>?
)