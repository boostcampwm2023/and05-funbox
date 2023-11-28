package com.rpg.funbox.presentation.game.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class QuizViewModel : ViewModel() {

    private val _location = MutableStateFlow<Pair<Double, Double>?>(null)
    val location = _location.asStateFlow()

    private val _latestAnswer = MutableStateFlow<String>("")
    val latestAnswer = _latestAnswer.asStateFlow()

    private val _quizUiEvent = MutableSharedFlow<QuizUiEvent>()
    val quizUiEvent = _quizUiEvent.asSharedFlow()

    private val _quizUiState = MutableStateFlow<QuizUiState>(QuizUiState())
    val quizUiState = _quizUiState.asStateFlow()

    fun setLocation(x: Double, y: Double) {
        _location.value = Pair(x, y)
    }

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
        _latestAnswer.value = answer.toString()
    }

    fun submitAnswer() {
        Timber.d("Answer: ${latestAnswer.value}")
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizAnswerSubmit)
        }
    }

    fun showAnswerCheckDialog() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizAnswerCheck)
        }
    }

    fun showNetworkAlertDialog() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizOtherUserDisconnected)
        }
    }

    fun showScoreBoardDialog() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizScoreBoard)
        }
    }
}