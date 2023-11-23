package com.rpg.funbox.presentation.game.wait

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.rpg.funbox.R

@BindingAdapter("app:setImage")
fun ImageView.bindImage(waitUiState: WaitUiState) {
    load(R.drawable.profile_game)
}