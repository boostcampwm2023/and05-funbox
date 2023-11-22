package com.example.funbox.presentation.game.quiz

sealed class QuizUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : QuizUiEvent()
    data object QuizAnswerSubmit : QuizUiEvent()
    data object QuizOtherUserDisconnected : QuizUiEvent()
    data object QuizAnswerCheck : QuizUiEvent()
    data object QuizScoreBoard : QuizUiEvent()
}