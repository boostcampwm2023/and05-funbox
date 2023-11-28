package com.rpg.funbox.presentation.game.quiz

data class QuizUiState(
    val answerValidState: Boolean = false,
    val userQuizState: UserQuizState = UserQuizState.Quiz
) {
    val isAnswerSubmitBtnEnable: Boolean = (answerValidState)
    val isUserQuizState: Boolean = (userQuizState == UserQuizState.Quiz)
}

enum class UserQuizState {
    Quiz, Answer
}