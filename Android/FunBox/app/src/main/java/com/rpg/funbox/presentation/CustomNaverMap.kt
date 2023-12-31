package com.rpg.funbox.presentation

import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.rpg.funbox.R

object CustomNaverMap {

    private const val MIN_ZOOM = 5.0
    private const val MAX_ZOOM = 20.0
    private const val SOUTH_WEST_LATITUDE = 31.43
    private const val SOUTH_WEST_LONGITUDE = 122.37
    private const val NORTH_EAST_LATITUDE = 44.35
    private const val NORTH_EAST_LONGITUDE = 132.0

    fun setNaverMap(naverMap: NaverMap, fusedLocationSource: FusedLocationSource): NaverMap {
        naverMap.locationSource = fusedLocationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Face
        naverMap.uiSettings.isLocationButtonEnabled = true

        naverMap.minZoom = MIN_ZOOM
        naverMap.maxZoom = MAX_ZOOM
        naverMap.uiSettings.isZoomControlEnabled = true

        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.icon = OverlayImage.fromResource(R.drawable.my_location)

        naverMap.extent = LatLngBounds(
            LatLng(SOUTH_WEST_LATITUDE, SOUTH_WEST_LONGITUDE), LatLng(
                NORTH_EAST_LATITUDE, NORTH_EAST_LONGITUDE
            )
        )

        return naverMap
    }

    fun setNaverMapLocationOverlay(naverMap: NaverMap): NaverMap {
        naverMap.locationOverlay.apply {
            isVisible = true
            iconHeight = 120
            iconWidth = 120
            icon = OverlayImage.fromResource(R.drawable.navi_icon)
        }

        return naverMap
    }
}