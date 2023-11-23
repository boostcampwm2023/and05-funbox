package com.rpg.funbox.presentation.game.quiz

data class QuizUiState(
    val answerValidState: Boolean = false
) {
    val isAnswerSubmitBtnEnable: Boolean = (answerValidState)
}