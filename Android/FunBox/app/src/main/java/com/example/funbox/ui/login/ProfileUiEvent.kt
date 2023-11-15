package com.example.funbox.ui.login

sealed class ProfileUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : ProfileUiEvent()
    object ProfileSuccess : ProfileUiEvent()

    object ProfileSelect : ProfileUiEvent()
}