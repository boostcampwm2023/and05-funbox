package com.rpg.funbox.presentation.game.quiz

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.rpg.funbox.R

@BindingAdapter("app:setImage")
fun ImageView.bindImage(image: String) {
    load(image)
}