package com.example.funbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _profileUiEvent = MutableSharedFlow<ProfileUiEvent>()
    val profileUiEvent = _profileUiEvent.asSharedFlow()

    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState())
    val profileUiState = _profileUiState.asStateFlow()

    fun startSelectProfile() {
        viewModelScope.launch {
            _profileUiEvent.emit(ProfileUiEvent.ProfileSelect)
        }
    }
}