package com.example.funbox.presentation.login.title

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TitleViewModel : ViewModel() {

    private val _titleUiEvent = MutableSharedFlow<TitleUiEvent>()
    val titleUiEvent = _titleUiEvent.asSharedFlow()

    private val _titleUiState = MutableStateFlow<TitleUiState>(TitleUiState())
    val titleUiState = _titleUiState.asStateFlow()

    fun startNaverLogin() {
        viewModelScope.launch {
            _titleUiEvent.emit(TitleUiEvent.NaverLoginStart)
        }
    }

    fun successNaverLogin() {
        viewModelScope.launch {
            _titleUiEvent.emit(TitleUiEvent.NaverLoginSuccess)
        }
    }
}