package com.example.funbox.presentation.login.profile

sealed class ProfileUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : ProfileUiEvent()
    data object ProfileSuccess : ProfileUiEvent()
    data object ProfileSelect : ProfileUiEvent()
}