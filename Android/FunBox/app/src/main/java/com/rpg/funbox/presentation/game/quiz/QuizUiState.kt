package com.rpg.funbox.presentation.game.quiz

data class QuizUiState(
    val waiting: Boolean = true,
    val finishWaiting: Boolean = false,
    val answerValidState: Boolean = false,
    val answerWriteState: Boolean = true,
    val userQuizState: UserQuizState = UserQuizState.Answer
) {
    val isAnswerSubmitBtnEnable: Boolean = (answerValidState)
    val isEtEnable: Boolean = (answerWriteState)
    val isUserQuizState: Boolean = (userQuizState == UserQuizState.Quiz)
}

enum class UserQuizState {
    Quiz, Answer
}