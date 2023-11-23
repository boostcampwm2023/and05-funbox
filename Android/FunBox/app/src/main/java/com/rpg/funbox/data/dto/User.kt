package com.rpg.funbox.data.dto

import com.naver.maps.geometry.LatLng

data class User(
    val status: Int,
    val id: Int,
    val loc: LatLng,
    val name: String?,
    val isMsg: Boolean
)
