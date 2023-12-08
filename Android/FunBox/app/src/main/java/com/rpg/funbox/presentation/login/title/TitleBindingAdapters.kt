package com.rpg.funbox.presentation.login.title

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.rpg.funbox.R

@SuppressLint("ResourceAsColor")
@BindingAdapter("app:image_tint")
fun ImageView.bindTint(titleUiState: TitleUiState) {
    when (titleUiState.networkSuccess) {
        false -> {
            imageTintList = ColorStateList.valueOf(R.color.disabled_color)
        }

        else -> {}
    }
}