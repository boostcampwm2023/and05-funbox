package com.rpg.funbox.presentation.game.quiz

data class QuizUiState(
    val waiting: Boolean = true,
    val finishWaiting: Boolean = false,
    val answerValidState: Boolean = false,
    val userQuizState: UserQuizState = UserQuizState.Answer
) {
    val isAnswerSubmitBtnEnable: Boolean = (answerValidState)
    val isUserQuizState: Boolean = (userQuizState == UserQuizState.Quiz)
}

enum class UserQuizState {
    Quiz, Answer
}