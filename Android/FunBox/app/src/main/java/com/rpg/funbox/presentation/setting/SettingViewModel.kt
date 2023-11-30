package com.rpg.funbox.presentation.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.R
import com.rpg.funbox.data.dto.UserInfoResponse
import com.rpg.funbox.data.repository.NaverLoginRepository
import com.rpg.funbox.data.repository.NaverLoginRepositoryImpl
import com.rpg.funbox.data.repository.UserRepository
import com.rpg.funbox.data.repository.UserRepositoryImpl
import com.rpg.funbox.presentation.login.nickname.NicknameUiEvent
import com.rpg.funbox.presentation.map.MapUiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {
    private val _settingUiEvent = MutableSharedFlow<SettingUiEvent>()
    val settingUiEvent = _settingUiEvent.asSharedFlow()

    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _user = MutableStateFlow(UserInfoResponse("", null,null))
    val user = _user

    private val _newName = MutableStateFlow("")
    val newName = _newName

    fun toMap() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.ToMap)
        }
    }

    fun setUserInfo(){
        //받아오기
        val response = UserInfoResponse("abc","def", "g")
        viewModelScope.launch {
            _user.update {
                it.copy(
                    userName = response.userName,
                    profileUrl = response.profileUrl
                )
            }
        }
    }

    fun draw(){
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.Draw)
        }
    }
    fun setName(){
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.SetName)
        }
    }
    private fun closeName(){
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.CloseName)
        }
    }
    fun setProfile(){
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.SetProfile)
        }
    }

    fun submitNickname() {
        viewModelScope.launch {
            Log.d("!!!",_newName.value)
            if (userRepository.patchUserName(userName = _newName.value)) {
                Log.d("!!!","a")
            }
        }
        closeName()
    }
}