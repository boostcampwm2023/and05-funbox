package com.rpg.funbox.presentation.map

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.rpg.funbox.data.dto.UserDetail
import com.naver.maps.map.overlay.InfoWindow

class MapProfileAdapter(
    private val pContext: Context,
    private val userDetail: UserDetail,
    private val image: Bitmap?,
) : InfoWindow.DefaultViewAdapter(pContext) {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun getContentView(p0: InfoWindow): View {
        val mapProfile = MapProfile(pContext)
        if(image !=null){
            mapProfile.setProfile(image)
        }
        mapProfile.invalidate()
        mapProfile.setMsg(userDetail.msg)
        mapProfile.setName(userDetail.name)


        return mapProfile
    }

}