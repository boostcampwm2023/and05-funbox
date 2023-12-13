package com.rpg.funbox.presentation.login.splash

sealed class SplashUiEvent {

    data class NetworkErrorEvent(val message: String = "Network Error") : SplashUiEvent()

    data object GetUsersLocationsSuccess : SplashUiEvent()

    data object Unauthorized : SplashUiEvent()
}
