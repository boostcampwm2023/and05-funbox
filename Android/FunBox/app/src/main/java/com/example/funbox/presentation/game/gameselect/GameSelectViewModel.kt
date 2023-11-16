package com.example.funbox.presentation.game.gameselect

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class GameSelectViewModel : ViewModel() {

    private val _gameSelectUiEvent = MutableSharedFlow<GameSelectUiEvent>()
    val gameSelectUiEvent = _gameSelectUiEvent.asSharedFlow()

    private val _gameSelectUiState = MutableStateFlow<GameSelectUiState>(GameSelectUiState())
    val gameSelectUiState = _gameSelectUiState.asStateFlow()


}