package com.rpg.funbox.data.dto

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker

data class User(
    val status: Int,
    val id: Int,
    var loc: LatLng,
    val name: String?,
    var isMsg: Boolean,
    var mapPin: Marker? = null
)
