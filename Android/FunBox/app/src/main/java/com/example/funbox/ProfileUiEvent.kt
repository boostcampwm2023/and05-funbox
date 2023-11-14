package com.example.funbox

sealed class ProfileUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : ProfileUiEvent()
    object ProfileSuccess : ProfileUiEvent()

    object ProfileSelect : ProfileUiEvent()
}