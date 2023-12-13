package com.rpg.funbox.presentation.setting

import com.rpg.funbox.presentation.login.nickname.NicknameValidState

data class SettingUiState(
    val nicknameValidState: NicknameValidState = NicknameValidState.None
) {
    val isSubmitBtnEnable: Boolean = (nicknameValidState == NicknameValidState.Valid)
}
