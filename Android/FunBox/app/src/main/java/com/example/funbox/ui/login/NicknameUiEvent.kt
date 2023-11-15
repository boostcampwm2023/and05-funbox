package com.example.funbox.ui.login

sealed class NicknameUiEvent {
    data class NetworkErrorEvent(val message: String = "Network Error") : NicknameUiEvent()
    object NicknameSuccess : NicknameUiEvent()
}