package com.example.funbox.presentation.game.wait

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.example.funbox.R

@BindingAdapter("app:setImage")
fun ImageView.bindImage(waitUiState: WaitUiState) {
    load(R.drawable.profile_game)
}