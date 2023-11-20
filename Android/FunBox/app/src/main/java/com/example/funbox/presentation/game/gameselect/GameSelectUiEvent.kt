package com.example.funbox.presentation.game.gameselect

sealed class GameSelectUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : GameSelectUiEvent()
    data object GameSelectSuccess : GameSelectUiEvent()
    data object GameListSubmit : GameSelectUiEvent()
}