package com.example.funbox.presentation.game.wait

sealed class WaitUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : WaitUiEvent()
}