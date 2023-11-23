package com.rpg.funbox.presentation.map

sealed class MapUiEvent {
    // data class NetworkErrorEvent(val message: String = "Network Error") : MapUiEvent()
    data object MessageOpen : MapUiEvent()
}