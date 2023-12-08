package com.rpg.funbox.presentation.setting

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.data.dto.UserInfoResponse
import com.rpg.funbox.data.repository.UserRepository
import com.rpg.funbox.data.repository.UserRepositoryImpl
import com.rpg.funbox.data.repository.UsersLocationRepository
import com.rpg.funbox.data.repository.UsersLocationRepositoryImpl
import com.rpg.funbox.presentation.map.MapUiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class SettingViewModel : ViewModel() {

    private val usersLocationRepository: UsersLocationRepository = UsersLocationRepositoryImpl()
    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _user = MutableStateFlow<UserInfoResponse?>(null)
    val user = _user.asStateFlow()

    private val _newName = MutableStateFlow<String>("")
    val newName = _newName

    private val _profileImageFile = MutableStateFlow<MultipartBody.Part?>(null)
    val profileImageFile = _profileImageFile.asStateFlow()

    private val _newProfileUri = MutableStateFlow<Uri?>(null)
    val newProfileUri = _newProfileUri.asStateFlow()

    private val _profileUri = MutableStateFlow<Uri?>(null)
    val profileUri = _profileUri.asStateFlow()

    private val _settingUiEvent = MutableSharedFlow<SettingUiEvent>()
    val settingUiEvent = _settingUiEvent.asSharedFlow()

    private fun closeSetNameDialog() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.CloseSetNameDialog)
        }
    }

    private fun closeSetProfileDialog() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.CloseSetProfileDialog)
        }
    }

    private fun setUserInfo() {
        viewModelScope.launch {
            val userInfo = userRepository.getUserInfo()
            userInfo?.let {
                _user.value = userInfo
            }
        }
    }

    fun goToMap() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.GoToMapFragment)
        }
    }

    fun submitLocation(locX: Double, locY: Double) {
        viewModelScope.launch {
            usersLocationRepository.getUsersLocation(locX, locY)
        }
    }

    fun initUserInfo() {
        viewModelScope.launch {
            val userInfo = userRepository.getUserInfo()
            userInfo?.let {
                _user.value = userInfo
                if (userInfo.profileUrl != null) {
                    _profileUri.value = "https://kr.object.ncloudstorage.com/funbox-profiles/${userInfo.profileUrl}".toUri()
                }
                _newProfileUri.value = _profileUri.value
            }
        }
    }

    fun setUserName() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.SetName)
        }
    }

    fun submitNewNickname() {
        viewModelScope.launch {
            if (userRepository.patchUserName(userName = _newName.value)) {
                setUserInfo()
            }
        }
        closeSetNameDialog()
    }

    fun setUserProfile() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.SetProfile)
        }
    }

    fun startSelectNewProfile() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.SelectProfile)
        }
    }

    fun selectNewProfile(uri: Uri?, body: MultipartBody.Part) {
        if (uri != null) {
            viewModelScope.launch {
                _newProfileUri.value = uri
                _profileImageFile.value = body
            }
        } else {
            _profileUri.value = null
        }
    }

    fun submitNewProfile() {
        viewModelScope.launch {
            _profileImageFile.value?.let { imageFile ->
                if (userRepository.postUserProfile(imageFile = imageFile)) {
                    _profileUri.value = _newProfileUri.value
                    setUserInfo()
                }
            }
        }
        closeSetProfileDialog()
    }

    fun startWithdrawal() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.StartWithdrawal)
        }
    }

    fun withdraw() {
        viewModelScope.launch {
            if (userRepository.withdraw()) {
                _settingUiEvent.emit(SettingUiEvent.Withdraw)
            }
        }
    }

    fun rejectGame() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.RejectGame)
        }
    }

    fun getGame() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.GetGame)
        }
    }

    fun toGame() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.ToGame)
        }
    }
}