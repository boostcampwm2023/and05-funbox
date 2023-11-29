package com.rpg.funbox.data.dto

data class UserLocation(
    val id: Int,
    val username: String,
    val locX: Double,
    val locY: Double,
    val isMsgInAnHour: Boolean
)
