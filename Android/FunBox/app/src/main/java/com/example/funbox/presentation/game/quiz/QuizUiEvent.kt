package com.example.funbox.presentation.game.quiz

sealed class QuizUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : QuizUiEvent()
}