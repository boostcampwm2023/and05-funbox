package com.example.funbox.data.dto

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker

data class User(
    val id: Int,
    val loc: LatLng,
    val name: String,
    val isMsg: Boolean,
    var mapPin: Marker? = null
)
