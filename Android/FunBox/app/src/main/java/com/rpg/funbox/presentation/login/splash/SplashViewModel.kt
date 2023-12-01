package com.rpg.funbox.presentation.login.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.data.repository.UserRepository
import com.rpg.funbox.data.repository.UserRepositoryImpl
import com.rpg.funbox.data.repository.UsersLocationRepository
import com.rpg.funbox.data.repository.UsersLocationRepositoryImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val usersLocationRepository: UsersLocationRepository = UsersLocationRepositoryImpl()
    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _splashUiEvent = MutableSharedFlow<SplashUiEvent>()
    val splashUiEvent = _splashUiEvent.asSharedFlow()

    private fun validateUserName(userName: String?): Boolean {
        return (userName != null)
    }

    fun getUsersLocations(locX: Double, locY: Double) {
        viewModelScope.launch {
            when (usersLocationRepository.getUsersLocation(locX, locY)) {
                null -> {
                    _splashUiEvent.emit(SplashUiEvent.Unauthorized)
                }
                else -> {
                    if (validateUserName(userRepository.getUserInfo())) {
                        _splashUiEvent.emit(SplashUiEvent.GetUsersLocationsSuccess)
                    } else {
                        _splashUiEvent.emit(SplashUiEvent.Unauthorized)
                    }
                }
            }
        }
    }
}