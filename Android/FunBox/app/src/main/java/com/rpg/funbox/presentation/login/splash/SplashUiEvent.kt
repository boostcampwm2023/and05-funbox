package com.rpg.funbox.presentation.login.splash

sealed class SplashUiEvent {

    data object GetUsersLocationsSuccess : SplashUiEvent()

    data object Unauthorized : SplashUiEvent()
}
