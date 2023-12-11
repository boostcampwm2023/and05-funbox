package com.rpg.funbox.presentation.game.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.rpg.funbox.data.dto.User
import com.rpg.funbox.data.dto.UserInfoResponse
import com.rpg.funbox.data.repository.UserRepository
import com.rpg.funbox.data.repository.UserRepositoryImpl
import com.rpg.funbox.data.repository.UsersLocationRepository
import com.rpg.funbox.data.repository.UsersLocationRepositoryImpl
import com.rpg.funbox.presentation.MapSocket
import com.rpg.funbox.presentation.map.Chat
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

    private val usersLocationRepository: UsersLocationRepository = UsersLocationRepositoryImpl()
    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _userState = MutableStateFlow<Boolean>(true)
    val userState = _userState.asStateFlow()

    private val _roomId = MutableStateFlow<String?>(null)
    val roomId = _roomId.asStateFlow()

    private val _user = MutableStateFlow<UserInfoResponse?>(null)
    val user = _user.asStateFlow()

    private val _otherUserId = MutableStateFlow<Int>(-1)
    val otherUserId = _otherUserId.asStateFlow()

    private val _otherUserInfo = MutableStateFlow<UserInfoResponse?>(null)
    val otherUserInfo = _otherUserInfo.asStateFlow()

    private val _prevOtherUser = MutableStateFlow<User?>(null)
    val prevOtherUser = _prevOtherUser.asStateFlow()

    private val _nowOtherUser = MutableStateFlow<User?>(null)
    val nowOtherUser = _nowOtherUser.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(listOf())
    val users = _users.asStateFlow()

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

    val chatAdapter = ChatAdapter()

    private val _chatMessages = MutableStateFlow<MutableList<MessageItem>>(mutableListOf())
    val chatMessages = _chatMessages.asStateFlow()

    val sendingMessage = MutableStateFlow<String>("")

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
        _latestAnswer.value = ""
    }

    fun setUserQuizStateTrue() {
        _quizUiState.update { uiState ->
            uiState.copy(answerWriteState = true)
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

    private fun receiveMessage() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.ReceiveMessage)
            chatAdapter.notifyDataSetChanged()
            //scrollToPosition(_chatMessages.value.size - 1)
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

                if (_userState.value) {
                    setFinalScore(Pair(json.first().score.toString(), json.last().score.toString()))
                } else {
                    setFinalScore(Pair(json.last().score.toString(), json.first().score.toString()))
                }
                showScoreBoard()
            }
            .on("quitGame") {
                alertNetworkError()
            }
            .on("lostConnection") {
                alertNetworkError()
            }
            .on("error") {
                setStateNetworkError()
            }
            .on("directMessage") {
                val json = Gson().fromJson(it[0].toString(), Chat::class.java)
                Timber.d("${json.message}")
                addMessage(MessageItem(1, json.message))
                receiveMessage()
            }
        Timber.d("이벤트 등록")
        setQuizGame()
    }

    fun addMessage(message:MessageItem){
        _chatMessages.value.add(message)
    }

    fun setUserState(state: Boolean) {
        _userState.value = state
    }

    fun setRoomId(newRoomId: String?) {
        _roomId.value = newRoomId
    }

    fun setUsersInfo(userId: Int) {
        Timber.d("Other User: $userId")
        _otherUserId.value = userId
        viewModelScope.launch {
            userRepository.getUserInfo()?.let { userInfo ->
                _user.value = userInfo
                Timber.d("${_user.value}")
            }
            userRepository.getSpecificUserInfo(userId)?.let { specificUserInfo ->
                _otherUserInfo.value = specificUserInfo
                Timber.d("$${_otherUserInfo.value}")
            }
        }
    }

    fun setUsersLocations(locX: Double, locY: Double) {
        viewModelScope.launch {
            _prevOtherUser.value = _nowOtherUser.value
            _nowOtherUser.value = null
            usersLocationRepository.getUsersLocation(locX, locY).userLocations?.let { locations ->
                locations.forEach { location ->
                    if ((location.id == _otherUserId.value) && (location.locX != null) && (location.locY != null)) {
                        _nowOtherUser.value = User(
                            200,
                            location.id,
                            LatLng(location.locX, location.locY),
                            location.username,
                            location.isMsgInAnHour,
                            mapPin = null
                        )
                    }
                }
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

    fun sendMessage() {
        viewModelScope.launch {
            _quizUiEvent.emit(QuizUiEvent.SendMessage)
            sendingMessage.value = ""
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