package com.example.funbox.presentation.login.nickname

sealed class NicknameUiEvent {
    data class NetworkErrorEvent(val message: String = "Network Error") : NicknameUiEvent()
    data object NicknameSuccess : NicknameUiEvent()
}