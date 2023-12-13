package com.rpg.funbox.presentation.login.profile

data class ProfileUiState(
    val profileValidState: ProfileValidState = ProfileValidState.None
) {
    val isBtnProfileEnable: Boolean = (profileValidState == ProfileValidState.Valid)
}

enum class ProfileValidState {
    None, Valid
}