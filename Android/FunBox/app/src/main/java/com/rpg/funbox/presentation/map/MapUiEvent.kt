package com.rpg.funbox.presentation.map

sealed class MapUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : MapUiEvent()

    data object LocationPermitted : MapUiEvent()

    data object MessageOpen : MapUiEvent()

    data object ToGame : MapUiEvent()

    data object RejectGame : MapUiEvent()

    data object GetGame : MapUiEvent()

    data object ToSetting : MapUiEvent()

    data object Toggle : MapUiEvent()

    data object GameStart : MapUiEvent()

    data object MessageSubmit : MapUiEvent()
}