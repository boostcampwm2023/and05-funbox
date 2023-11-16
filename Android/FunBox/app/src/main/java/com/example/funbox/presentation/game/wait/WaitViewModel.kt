package com.example.funbox.presentation.game.wait

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class WaitViewModel : ViewModel() {

    private val _waitUiEvent = MutableSharedFlow<WaitUiEvent>()
    val waitUiEvent = _waitUiEvent.asSharedFlow()

    private val _waitUiState = MutableStateFlow<WaitUiState>(WaitUiState())
    val waitUiState = _waitUiState.asStateFlow()


}