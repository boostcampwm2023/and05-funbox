package com.rpg.funbox.presentation.game.quiz

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView

@BindingAdapter("app:answer_card_visible")
fun MaterialCardView.bindAnswerVisibility(userQuizState: QuizUiState) {
    visibility = when (userQuizState.isUserQuizState) {
        true -> {
            View.GONE
        }

        false -> {
            View.VISIBLE
        }
    }
}

@BindingAdapter("app:quiz_card_visible")
fun MaterialCardView.bindQuizVisibility(userQuizState: QuizUiState) {
    visibility = when (userQuizState.isUserQuizState) {
        true -> {
            View.VISIBLE
        }

        false -> {
            View.GONE
        }
    }
}