package com.example.funbox.presentation.login

sealed class NicknameUiEvent {
    data class NetworkErrorEvent(val message: String = "Network Error") : NicknameUiEvent()
    object NicknameSuccess : NicknameUiEvent()
}