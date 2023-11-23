package com.example.funbox.presentation.map

sealed class MapUiEvent {
    // data class NetworkErrorEvent(val message: String = "Network Error") : MapUiEvent()
    data object MessageOpen : MapUiEvent()
    data object ToGame : MapUiEvent()

    data object GetGame: MapUiEvent()
}