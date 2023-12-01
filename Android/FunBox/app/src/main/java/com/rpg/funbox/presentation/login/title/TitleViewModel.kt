package com.rpg.funbox.presentation.login.title

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.data.dto.UserAuthDto
import com.rpg.funbox.data.repository.NaverLoginRepository
import com.rpg.funbox.data.repository.NaverLoginRepositoryImpl
import com.rpg.funbox.data.repository.UserRepository
import com.rpg.funbox.data.repository.UserRepositoryImpl
import com.rpg.funbox.presentation.login.nickname.NicknameUiEvent
import com.rpg.funbox.presentation.login.nickname.NicknameUiState
import com.rpg.funbox.presentation.login.nickname.NicknameValidState
import com.rpg.funbox.presentation.login.profile.ProfileUiEvent
import com.rpg.funbox.presentation.login.profile.ProfileUiState
import com.rpg.funbox.presentation.login.profile.ProfileValidState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber

class TitleViewModel : ViewModel() {

    private val naverLoginRepository: NaverLoginRepository = NaverLoginRepositoryImpl()
    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _userAuthDto = MutableStateFlow<UserAuthDto?>(null)
    val userAuthDto = _userAuthDto.asStateFlow()

    private val userName = MutableStateFlow<String>("")

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri = _profileImageUri.asStateFlow()

    private val profileUrl = MutableStateFlow<MultipartBody.Part?>(null)

    private val _titleUiEvent = MutableSharedFlow<TitleUiEvent>()
    val titleUiEvent = _titleUiEvent.asSharedFlow()

    private val _nicknameUiEvent = MutableSharedFlow<NicknameUiEvent>()
    val nicknameUiEvent = _nicknameUiEvent.asSharedFlow()

    private val _profileUiEvent = MutableSharedFlow<ProfileUiEvent>()
    val profileUiEvent = _profileUiEvent.asSharedFlow()

    private val _titleUiState = MutableStateFlow<TitleUiState>(TitleUiState())
    val titleUiState = _titleUiState.asStateFlow()

    private val _nicknameUiState = MutableStateFlow<NicknameUiState>(NicknameUiState())
    val nicknameUiState = _nicknameUiState.asStateFlow()

    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState())
    val profileUiState = _profileUiState.asStateFlow()

    fun startNaverLogin() {
        viewModelScope.launch {
            _titleUiEvent.emit(TitleUiEvent.NaverLoginStart)
        }
    }

    fun submitAccessToken(token: String) {
        viewModelScope.launch {
            _userAuthDto.value = naverLoginRepository.postNaverAccessToken(token)
            when (userRepository.getUserInfo()) {
                null -> {
                    _titleUiEvent.emit(TitleUiEvent.SignUpStart)
                }

                else -> {
                    _titleUiEvent.emit(TitleUiEvent.NaverLoginSuccess)
                }
            }
        }
    }

    fun validateUserNickname(id: CharSequence) {
        if (id.isBlank()) {
            _nicknameUiState.update { uiState ->
                uiState.copy(nicknameValidState = NicknameValidState.None)
            }
        }
        else {
            _nicknameUiState.update { uiState ->
                uiState.copy(nicknameValidState = NicknameValidState.Valid)
            }
        }
        userName.value = id.toString()
    }

    fun submitNickname() {
        viewModelScope.launch {
            Timber.d("User Name: ${userName.value}")
            if (userRepository.patchUserName(userName = userName.value)) {
                _nicknameUiEvent.emit(NicknameUiEvent.NicknameSubmit)
            }
        }
    }

    fun startSelectProfile() {
        viewModelScope.launch {
            _profileUiEvent.emit(ProfileUiEvent.ProfileSelect)
        }
    }

    fun selectProfile(uri: Uri?, body: MultipartBody.Part) {
        if (uri == null) {
            _profileUiState.update { uiState ->
                uiState.copy(profileValidState = ProfileValidState.None)
            }
        }
        else {
            _profileImageUri.value = uri
            profileUrl.value = body
            _profileUiState.update { uiState ->
                uiState.copy(profileValidState = ProfileValidState.Valid)
            }
        }
    }

    fun submitProfile() {
        viewModelScope.launch {
            when (profileUrl.value?.let { userRepository.postUserProfile(it) }) {
                true -> {
                    _profileUiEvent.emit(ProfileUiEvent.ProfileSubmit)
                }

                false -> {
                    _profileUiEvent.emit(ProfileUiEvent.NetworkErrorEvent())
                }

                else -> {}
            }
        }
    }
}