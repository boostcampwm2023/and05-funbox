package com.rpg.funbox.presentation.setting

import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import coil.load
import com.rpg.funbox.R
import java.lang.Exception

object SettingBindingAdapter {
    @BindingAdapter("app:imageUrl")
    @JvmStatic
    fun loadImage(view: ImageButton, url:String?){
        try {
            view.load(url)
        }catch (e: Exception){
            view.load(R.drawable.profile_none)
        }
    }

}