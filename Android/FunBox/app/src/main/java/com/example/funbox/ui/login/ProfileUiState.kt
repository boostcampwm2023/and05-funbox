package com.example.funbox.ui.login

data class ProfileUiState(
    val profileValidState: ProfileValidState = ProfileValidState.None
) {
    val isBtnProfileEnable: Boolean = (profileValidState == ProfileValidState.Valid)
}

enum class ProfileValidState {
    None, Valid
}