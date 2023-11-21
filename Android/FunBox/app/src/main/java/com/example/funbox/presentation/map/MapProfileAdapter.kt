package com.example.funbox.presentation.map

import android.content.Context
import android.util.Log
import android.view.View
import com.example.funbox.data.dto.UserDetail
import com.naver.maps.map.overlay.InfoWindow

class MapProfileAdapter(private val pContext: Context, private val userDetail:UserDetail) : InfoWindow.DefaultViewAdapter(pContext) {

    override fun getContentView(p0: InfoWindow): View {
        val mapProfile = MapProfile(pContext)

        mapProfile.setProfile(userDetail.profile)
        mapProfile.setMsg(userDetail.msg)
        mapProfile.setName(userDetail.name)
        return mapProfile
    }

}