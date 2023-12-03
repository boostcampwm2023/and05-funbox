package com.rpg.funbox.presentation.setting

import android.net.Uri
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.rpg.funbox.R
import java.lang.Exception

@BindingAdapter("app:imageUrlBtn")
fun ImageButton.loadImage(uri: Uri?) {
    try {
        load(uri)
    } catch (e: Exception) {
        load(R.drawable.profile_none)
    }
}

@BindingAdapter("app:imageUrl")
fun ImageView.loadImage(uri: Uri?) {
    try {
        load(uri)
    } catch (e: Exception) {
        load(R.drawable.profile_none)
    }
}