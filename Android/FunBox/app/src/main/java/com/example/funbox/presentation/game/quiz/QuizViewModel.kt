package com.example.funbox.presentation.game.quiz

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class QuizViewModel : ViewModel() {

    private val latestAnswer = MutableStateFlow<String>("")

    private val _quizUiEvent = MutableSharedFlow<QuizUiEvent>()
    val quizUiEvent = _quizUiEvent.asSharedFlow()

    private val _quizUiState = MutableStateFlow<QuizUiState>(QuizUiState())
    val quizUiState = _quizUiState.asStateFlow()

    fun validateAnswer(answer: CharSequence) {
        if (answer.isBlank()) {
            _quizUiState.update { uiState ->
                uiState.copy(answerValidState = false)
            }
        } else {
            _quizUiState.update { uiState ->
                uiState.copy(answerValidState = true)
            }
        }
        latestAnswer.value = answer.toString()
    }

    fun submitAnswer() {
        Timber.d("Answer: ${latestAnswer.value}")
    }
}