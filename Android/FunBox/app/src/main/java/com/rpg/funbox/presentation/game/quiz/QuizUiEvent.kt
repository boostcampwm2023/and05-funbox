package com.rpg.funbox.presentation.game.quiz

sealed class QuizUiEvent {

    data class NetworkErrorEvent(val message: String = "네트워크에 문제가 있습니다.") : QuizUiEvent()

    data object WaitSuccess : QuizUiEvent()

    data object QuizAnswerSubmit : QuizUiEvent()

    data object QuizNetworkDisconnected : QuizUiEvent()

    data object QuizAnswerCheckStart : QuizUiEvent()

    data object QuizAnswerCheckRight : QuizUiEvent()

    data object QuizAnswerCheckWrong : QuizUiEvent()

    data object QuizScoreBoard : QuizUiEvent()

    data object QuizFinish : QuizUiEvent()

    data object SendMessage : QuizUiEvent()

    data object ReceiveMessage : QuizUiEvent()
}