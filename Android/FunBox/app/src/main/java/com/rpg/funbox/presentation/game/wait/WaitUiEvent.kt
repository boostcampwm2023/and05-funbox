package com.rpg.funbox.presentation.game.wait

sealed class WaitUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : WaitUiEvent()
    data object WaitSuccess : WaitUiEvent()
}