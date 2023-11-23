package com.rpg.funbox.presentation.login.profile

import android.net.Uri
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.rpg.funbox.R

@BindingAdapter("app:profile_image_uri")
fun ImageButton.bindImageUri(uri: Uri?) {
    if (uri == null) {
        setImageResource(R.drawable.group_1940)
    } else {
        load(uri) {
            scale(Scale.FILL)
            crossfade(enable = true)
            transformations(RoundedCornersTransformation(10F))
        }
    }
}