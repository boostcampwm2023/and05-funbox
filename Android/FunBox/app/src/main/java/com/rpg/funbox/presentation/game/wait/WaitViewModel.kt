package com.rpg.funbox.presentation.game.wait

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.presentation.map.MapUiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class WaitViewModel : ViewModel() {

    private val _roomId = MutableStateFlow<String?>(null)
    val roomId = _roomId.asStateFlow()

    private val _waitUiEvent = MutableSharedFlow<WaitUiEvent>()
    val waitUiEvent = _waitUiEvent.asSharedFlow()

    private val _waitUiState = MutableStateFlow<WaitUiState>(WaitUiState())
    val waitUiState = _waitUiState.asStateFlow()

    private val _userState = MutableStateFlow<Boolean>(true)
    val userState = _userState.asStateFlow()

    fun setRoomId(newRoomId: String?) {
        _roomId.value = newRoomId
    }

    fun toGame() {
        viewModelScope.launch {
            Timber.d("User State: ${_userState.value}")
            _waitUiEvent.emit(WaitUiEvent.WaitSuccess)
        }
    }

    fun setUserState(state: Boolean) {
        _userState.value = state
//        if (!_userState.value) {
//            Timber.d("User State: ${_userState.value}")
//            viewModelScope.launch {
//                _waitUiEvent.emit(WaitUiEvent.WaitSuccess)
//            }
//        }
    }
}