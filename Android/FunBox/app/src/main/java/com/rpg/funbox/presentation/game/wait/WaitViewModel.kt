package com.rpg.funbox.presentation.game.wait

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.presentation.map.MapUiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WaitViewModel : ViewModel() {

    private val _waitUiEvent = MutableSharedFlow<WaitUiEvent>()
    val waitUiEvent = _waitUiEvent.asSharedFlow()

    private val _waitUiState = MutableStateFlow<WaitUiState>(WaitUiState())
    val waitUiState = _waitUiState.asStateFlow()

    private val _userState = MutableSharedFlow<Boolean>()
    val userState = _userState.asSharedFlow()

    fun toGame(){
        viewModelScope.launch {
            _waitUiEvent.emit(WaitUiEvent.WaitSuccess)
        }
    }

    fun setUserState(state:Boolean){
        viewModelScope.launch {
            _userState.emit(state)
        }
    }
}