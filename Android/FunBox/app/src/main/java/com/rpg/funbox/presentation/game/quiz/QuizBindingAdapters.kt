package com.rpg.funbox.presentation.game.quiz

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.rpg.funbox.R

@BindingAdapter("app:setImage")
fun ImageView.bindImage(image: String?) {
    load(R.drawable.profile_game)
}

@BindingAdapter("app:question_marquee")
fun TextView.bindMarquee(quizUiState: QuizUiState) {
    isSelected = true
}