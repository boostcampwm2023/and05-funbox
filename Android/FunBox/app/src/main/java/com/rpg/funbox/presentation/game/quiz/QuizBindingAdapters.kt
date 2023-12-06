package com.rpg.funbox.presentation.game.quiz

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import coil.load
import com.rpg.funbox.R

@BindingAdapter("app:setImage")
fun ImageView.bindImage(url: String?) {
    if (url == null) {
        load(R.drawable.profile_none)
    } else {
        load("https://kr.object.ncloudstorage.com/funbox-profiles/${url}".toUri())
    }
}

@BindingAdapter("app:question_marquee")
fun TextView.bindMarquee(quizUiState: QuizUiState) {
    isSelected = true
}

@BindingAdapter("app:delete_text")
fun EditText.deleteText(quizUiState: QuizUiState) {
    if (!quizUiState.isEtEnable) {
        text = null
    }
}