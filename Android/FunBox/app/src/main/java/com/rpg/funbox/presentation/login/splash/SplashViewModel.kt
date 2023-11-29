package com.rpg.funbox.presentation.login.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.data.repository.UsersLocationRepository
import com.rpg.funbox.data.repository.UsersLocationRepositoryImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val usersLocationRepository: UsersLocationRepository = UsersLocationRepositoryImpl()

    private val _splashUiEvent = MutableSharedFlow<SplashUiEvent>()
    val splashUiEvent = _splashUiEvent.asSharedFlow()

    fun getUsersLocations() {
        viewModelScope.launch {
            when (usersLocationRepository.getUsersLocation()) {
                null -> {
                    _splashUiEvent.emit(SplashUiEvent.Unauthorized)
                }
                else -> {
                    _splashUiEvent.emit(SplashUiEvent.GetUsersLocationsSuccess)
                }
            }
        }
    }
}