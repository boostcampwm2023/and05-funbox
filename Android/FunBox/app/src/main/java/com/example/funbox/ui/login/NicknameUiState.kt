package com.example.funbox.ui.login

data class NicknameUiState(
    val nicknameValidState: NicknameValidState = NicknameValidState.None
) {
    val isNextBtnEnable: Boolean = (nicknameValidState == NicknameValidState.Valid)
}

enum class NicknameValidState {
    None, Valid
}