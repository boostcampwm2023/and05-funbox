package com.example.funbox.data

import com.naver.maps.geometry.LatLng

data class User(
    val id: Int,
    val loc: LatLng,
    val name: String,
    val isMsg: Boolean,
)
