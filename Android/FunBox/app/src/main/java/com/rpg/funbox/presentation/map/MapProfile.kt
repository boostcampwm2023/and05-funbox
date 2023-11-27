package com.rpg.funbox.presentation.map

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogMessageBinding
import com.rpg.funbox.databinding.MapProfileBinding
import com.naver.maps.map.MapView
import java.net.URI

class MapProfile @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val layoutInflater = LayoutInflater.from(context)
    private val binding: MapProfileBinding = MapProfileBinding.inflate(layoutInflater, this, true)

    @RequiresApi(Build.VERSION_CODES.P)
    fun setProfile(uri: String) {
//        val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, Uri.parse("https://firebasestorage.googleapis.com/v0/b/chattingservice-59c1f.appspot.com/o/%EC%B9%B4%EB%A6%AC%EB%82%98.jpg?alt=media&token=5e74df56-b9ba-4e8d-9b33-37fe7772296c")))
//        binding.ivProfile.load(bitmap)
    }

    fun setName(text: String) {
        binding.tvProfileName.text = text
    }

    fun setMsg(text: String) {
        binding.tvProfileMsg.text = text
    }

}