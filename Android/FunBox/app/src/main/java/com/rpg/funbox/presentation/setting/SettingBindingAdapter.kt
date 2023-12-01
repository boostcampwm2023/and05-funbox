package com.rpg.funbox.presentation.setting

import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import coil.load
import com.rpg.funbox.R
import java.lang.Exception

@BindingAdapter("app:imageUrlBtn")
fun loadImage(view: ImageButton, url:String?){
    try {
        view.load(url)
    }catch (e: Exception){
        view.load(R.drawable.profile_none)
    }
}

@BindingAdapter("app:imageUrl")
fun loadImage(view: ImageView, url:String?){
    try {
        view.load(url)
    }catch (e: Exception){
        view.load(R.drawable.profile_none)
    }
}