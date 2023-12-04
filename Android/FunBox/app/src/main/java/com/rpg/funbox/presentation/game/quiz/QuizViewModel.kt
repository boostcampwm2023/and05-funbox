package com.rpg.funbox.presentation.game.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.data.repository.UserRepository
import com.rpg.funbox.data.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _roomId = MutableStateFlow<String?>(null)
    val roomId = _roomId.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName = _userName.asStateFlow()

    private val _otherUserName = MutableStateFlow<String?>(null)
    val otherUserName = _otherUserName.asStateFlow()

    private val _location = MutableStateFlow<Pair<Double, Double>?>(null)
    val location = _location.asStateFlow()

    private val _latestQuiz = MutableStateFlow<String>("")
    val latestQuiz = _latestQuiz.asStateFlow()

    private val _latestAnswer = MutableStateFlow<String>("")
    val latestAnswer = _latestAnswer.asStateFlow()

    private val _finalScore = MutableStateFlow<Pair<String, String>>(Pair("", ""))
    val finalScore = _finalScore.asStateFlow()

    private val _quizUiEvent = MutableSharedFlow<QuizUiEvent>()
    val quizUiEvent = _quizUiEvent.asSharedFlow()

    private val _quizUiState = MutableStateFlow<QuizUiState>(QuizUiState())
    val quizUiState = _quizUiState.asStateFlow()

    fun setRoomId(newRoomId: String?) {
        _roomId.value = newRoomId
    }

    fun setUserNames(userId: Int) {
        viewModelScope.launch {
            userRepository.getUserInfo()?.let { userInfo ->
                _userName.value = userInfo.userName
            }
            userRepository.getSpecificUserInfo(userId)?.let { specificUserInfo ->
                _otherUserName.value = specificUserInfo.userName
            }
        }
    }

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

    fun setUserQuizState(userQuizState: UserQuizState) {
        _quizUiState.update { uiState ->
            uiState.copy(userQuizState = userQuizState)
        }
    }

    fun setLatestQuiz(quiz: String) {
        _latestQuiz.value = quiz
    }

    fun submitAnswer() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizAnswerSubmit)
        }
    }

    fun checkAnswerCorrect() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizAnswerCheckStart)
        }
    }

    fun setLatestAnswer(answer: String) {
        _latestAnswer.value = answer
    }

    fun checkAnswerRight() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizAnswerCheckRight)
        }
    }

    fun checkAnswerWrong() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizAnswerCheckWrong)
        }
    }

    fun alertNetworkError() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizNetworkDisconnected)
        }
    }

    fun setFinalScore(scorePair: Pair<String, String>) {
        _finalScore.value = scorePair
    }

    fun showScoreBoard() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizScoreBoard)
        }
    }

    fun finishQuiz() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizFinish)
        }
    }

    fun setStateNetworkError() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.NetworkErrorEvent())
        }
    }
}