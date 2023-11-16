package com.example.funbox.presentation.login.title

sealed class TitleUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : TitleUiEvent()

    data object NaverLoginStart : TitleUiEvent()
    data object NaverLoginSuccess : TitleUiEvent()
}