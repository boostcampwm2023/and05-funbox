package com.example.funbox.presentation.game.quiz

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class QuizViewModel : ViewModel() {

    private val _quizUiEvent = MutableSharedFlow<QuizUiEvent>()
    val quizUiEvent = _quizUiEvent.asSharedFlow()

    private val _quizUiState = MutableStateFlow<QuizUiState>(QuizUiState())
    val quizUiState = _quizUiState.asStateFlow()


}