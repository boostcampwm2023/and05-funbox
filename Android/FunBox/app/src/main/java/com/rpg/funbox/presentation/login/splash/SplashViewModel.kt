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
import timber.log.Timber

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
            try {
                usersLocationRepository.getUsersLocation(locX, locY)?.let { userLocationResponse ->
                    when (userLocationResponse.resultMessage) {
                        "OK" -> {
                            if (validateUserName(userRepository.getUserInfo()?.userName)) {
                                _splashUiEvent.emit(SplashUiEvent.GetUsersLocationsSuccess)
                                Timber.d("OK")
                            } else {
                                _splashUiEvent.emit(SplashUiEvent.Unauthorized)
                                Timber.d("No Nickname")
                            }
                        }

                        "Unauthorized" -> {
                            _splashUiEvent.emit(SplashUiEvent.Unauthorized)
                            Timber.d("Unauthorized")
                        }

                        else -> {
                            _splashUiEvent.emit(SplashUiEvent.NetworkErrorEvent())
                            Timber.d("else")
                        }
                    }
                }
            } catch (e: Exception) {
                _splashUiEvent.emit(SplashUiEvent.NetworkErrorEvent())
            }
        }
    }
}