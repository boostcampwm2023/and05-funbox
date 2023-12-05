package com.rpg.funbox.presentation.game.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rpg.funbox.data.dto.UserInfoResponse
import com.rpg.funbox.data.repository.UserRepository
import com.rpg.funbox.data.repository.UserRepositoryImpl
import com.rpg.funbox.presentation.MapSocket
import com.rpg.funbox.presentation.map.GameApplyAnswerFromServerData
import com.rpg.funbox.presentation.map.QuizAnswerFromServer
import com.rpg.funbox.presentation.map.QuizFromServer
import com.rpg.funbox.presentation.map.ScoreFromServer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class QuizViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _userState = MutableStateFlow<Boolean>(true)
    val userState = _userState.asStateFlow()

    private val _roomId = MutableStateFlow<String?>(null)
    val roomId = _roomId.asStateFlow()

    private val _user = MutableStateFlow<UserInfoResponse?>(null)
    val user = _user.asStateFlow()

    private val _otherUserId = MutableStateFlow<Int>(-1)
    val otherUserId = _otherUserId.asStateFlow()

    private val _otherUser = MutableStateFlow<UserInfoResponse?>(null)
    val otherUser = _otherUser.asStateFlow()

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

    private fun setQuizGame() {
        viewModelScope.launch {
            if (userState.value) {
                Timber.d("게임 ㄱㄱ")
            } else {
                roomId.value?.let {
                    Timber.d("Accept 전")
                    MapSocket.acceptGame(it)
                    Timber.d("Accept 후")
                }
            }
        }
    }

    private fun toGame() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.WaitSuccess)
        }
    }

    private fun setUserQuizState(userQuizState: UserQuizState) {
        _quizUiState.update { uiState ->
            uiState.copy(answerWriteState = true, userQuizState = userQuizState)
        }
    }

    private fun setLatestQuiz(quiz: String) {
        _latestQuiz.value = quiz
    }

    private fun checkAnswerCorrect() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizAnswerCheckStart)
        }
    }

    private fun setLatestAnswer(answer: String) {
        _latestAnswer.value = answer
    }

    private fun setStateNetworkError() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.NetworkErrorEvent())
        }
    }

    private fun alertNetworkError() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizNetworkDisconnected)
        }
    }

    private fun setFinalScore(scorePair: Pair<String, String>) {
        _finalScore.value = scorePair
    }

    private fun showScoreBoard() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizScoreBoard)
        }
    }

    fun connectSocket(myUserId: Int) {
        MapSocket.mSocket.on("location") {
            Timber.tag("LOCATION").d(it[0].toString())
        }
            .on("gameApplyAnswer") {
                val json =
                    Gson().fromJson(it[0].toString(), GameApplyAnswerFromServerData::class.java)
                Timber.tag("gameApplyAnswer").d(json.answer)
                when (json.answer) {
                    "OFFLINE" -> {
                        finishQuiz()
                    }

                    "ACCEPT" -> {
                        toGame()
                    }

                    "REJECT" -> {
                        finishQuiz()
                    }
                }
            }
            .on("quiz") {
                val json = Gson().fromJson(it[0].toString(), QuizFromServer::class.java)
                Timber.d(json.toString())
                Timber.d(json.quiz)
                Timber.d(json.target.toString())
                Timber.d("$myUserId")
                setRoomId(json.roomId)
                Timber.d("Rood Id: ${roomId.value}, Target: ${json.target}")

                when (json.target) {
                    myUserId -> {
                        Timber.d("답 맞춤")
                        setUserQuizState(UserQuizState.Answer)
                    }

                    else -> {
                        Timber.d("문제를 냄")
                        setLatestQuiz(json.quiz)
                        setUserQuizState(UserQuizState.Quiz)
                    }
                }
            }
            .on("quizAnswer") {
                val json = Gson().fromJson(it[0].toString(), QuizAnswerFromServer::class.java)
                Timber.d(json.answer)
                setRoomId(json.roomId)

                setLatestAnswer(json.answer)
                checkAnswerCorrect()
            }
            .on("score") {
                val json = Gson().fromJson(it[0].toString(), ScoreFromServer::class.java)
                Timber.d(json.first().toString())
                Timber.d(json.last().toString())

                setFinalScore(Pair(json.first().score.toString(), json.last().score.toString()))
                showScoreBoard()
            }
            .on("lostConnection") {
                Timber.d(it.toString())

                alertNetworkError()
            }
            .on("error") {
                Timber.tag("ERROR").e(it[0].toString())

                setStateNetworkError()
            }
        Timber.d("이벤트 등록")
        setQuizGame()
    }

    fun setUserState(state: Boolean) {
        _userState.value = state
    }

    fun setRoomId(newRoomId: String?) {
        _roomId.value = newRoomId
    }

    fun setUserNames(userId: Int) {
        _otherUserId.value = userId
        viewModelScope.launch {
            userRepository.getUserInfo()?.let { userInfo ->
                _user.value= userInfo
            }
            userRepository.getSpecificUserInfo(userId)?.let { specificUserInfo ->
                _otherUser.value= specificUserInfo
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

    fun submitAnswer() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizAnswerSubmit)
            _quizUiState.update { uiState ->
                uiState.copy(answerValidState = false, answerWriteState = false)
            }
            _latestAnswer.value = ""
        }
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

    fun finishQuiz() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.QuizFinish)
        }
    }
}