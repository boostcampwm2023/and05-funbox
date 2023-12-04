package com.rpg.funbox.presentation.setting

import android.net.Uri
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.rpg.funbox.R
import timber.log.Timber

@BindingAdapter("app:imageUrlBtn")
fun ImageButton.loadImage(uri: Uri?) {
    if (uri == null) {
        load(R.drawable.profile_none)
    } else {
        load(uri)
    }
}

@BindingAdapter("app:imageUrl")
fun ImageView.loadImage(uri: Uri?) {
    if (uri == null) {
        load(R.drawable.profile_none)
    } else {
        load(uri)
    }
}