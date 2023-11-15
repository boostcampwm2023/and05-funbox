package com.example.funbox.presentation.login.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    fun successSelectProfile(uri: Uri?) {
        if (uri == null) {
            _profileUiState.update { uiState ->
                uiState.copy(profileValidState = ProfileValidState.None)
            }
        }
        else {
            _profileUiState.update { uiState ->
                uiState.copy(profileValidState = ProfileValidState.Valid)
            }
        }
    }
}